"""
# WebAPI文档

` python api_v2.py -a 127.0.0.1 -p 9880 -c GPT_SoVITS/configs/tts_infer.yaml `

## 执行参数:
    `-a` - `绑定地址, 默认"127.0.0.1"`
    `-p` - `绑定端口, 默认9880`
    `-c` - `TTS配置文件路径, 默认"GPT_SoVITS/configs/tts_infer.yaml"`

## 调用:

### 推理

endpoint: `/tts`
GET:
```
http://127.0.0.1:9880/tts?text=先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。&text_lang=zh&ref_audio_path=archive_jingyuan_1.wav&prompt_lang=zh&prompt_text=我是「罗浮」云骑将军景元。不必拘谨，「将军」只是一时的身份，你称呼我景元便可&text_split_method=cut5&batch_size=1&media_type=wav&streaming_mode=true
```

POST:
```json
{
    "text": "",                   # str.(required) text to be synthesized
    "text_lang: "",               # str.(required) language of the text to be synthesized
    "ref_audio_path": "",         # str.(required) reference audio path
    "aux_ref_audio_paths": [],    # list.(optional) auxiliary reference audio paths for multi-speaker tone fusion
    "prompt_text": "",            # str.(optional) prompt text for the reference audio
    "prompt_lang": "",            # str.(required) language of the prompt text for the reference audio
    "top_k": 5,                   # int. top k sampling
    "top_p": 1,                   # float. top p sampling
    "temperature": 1,             # float. temperature for sampling
    "text_split_method": "cut0",  # str. text split method, see text_segmentation_method.py for details.
    "batch_size": 1,              # int. batch size for inference
    "batch_threshold": 0.75,      # float. threshold for batch splitting.
    "split_bucket: True,          # bool. whether to split the batch into multiple buckets.
    "speed_factor":1.0,           # float. control the speed of the synthesized audio.
    "streaming_mode": False,      # bool. whether to return a streaming response.
    "seed": -1,                   # int. random seed for reproducibility.
    "parallel_infer": True,       # bool. whether to use parallel inference.
    "repetition_penalty": 1.35    # float. repetition penalty for T2S model.
}
```

RESP:
成功: 直接返回 wav 音频流， http code 200
失败: 返回包含错误信息的 json, http code 400

### 命令控制

endpoint: `/control`

command:
"restart": 重新运行
"exit": 结束运行

GET:
```
http://127.0.0.1:9880/control?command=restart
```
POST:
```json
{
    "command": "restart"
}
```

RESP: 无


### 切换GPT模型

endpoint: `/set_gpt_weights`

GET:
```
http://127.0.0.1:9880/set_gpt_weights?weights_path=GPT_SoVITS/pretrained_models/s1bert25hz-2kh-longer-epoch=68e-step=50232.ckpt
```
RESP: 
成功: 返回"success", http code 200
失败: 返回包含错误信息的 json, http code 400


### 切换Sovits模型

endpoint: `/set_sovits_weights`

GET:
```
http://127.0.0.1:9880/set_sovits_weights?weights_path=GPT_SoVITS/pretrained_models/s2G488k.pth
```

RESP: 
成功: 返回"success", http code 200
失败: 返回包含错误信息的 json, http code 400
    
"""
import os
import sys
import tempfile
import threading
import traceback

from tempfile import NamedTemporaryFile
from typing import Generator
import os
import time
import requests
from apscheduler.schedulers.background import BackgroundScheduler
from pydub import AudioSegment
from starlette.middleware.cors import CORSMiddleware

import my_utils
from config import python_exec

# from webui import open_asr
from tools.asr.config import asr_dict

now_dir = os.getcwd()
sys.path.append(now_dir)
sys.path.append("%s/GPT_SoVITS" % (now_dir))
from subprocess import Popen
import argparse
import shutil
import subprocess
import wave
import signal
import numpy as np
import soundfile as sf
from fastapi import FastAPI, Request, HTTPException, Response
from fastapi.responses import StreamingResponse, JSONResponse
from fastapi import FastAPI, UploadFile, File
import uvicorn
from io import BytesIO
from tools.i18n.i18n import I18nAuto
from GPT_SoVITS.TTS_infer_pack.TTS import TTS, TTS_Config
from GPT_SoVITS.TTS_infer_pack.text_segmentation_method import get_method_names as get_cut_method_names
from fastapi.responses import StreamingResponse
from pydantic import BaseModel
# print(sys.path)
# -*- coding=utf-8
from qcloud_cos import CosConfig
from qcloud_cos import CosS3Client
from qcloud_cos.cos_exception import CosClientError, CosServiceError
import sys
import os
import logging
from dotenv import load_dotenv
import os

# 正常情况日志级别使用 INFO，需要定位时可以修改为 DEBUG，此时 SDK 会打印和服务端的通信信息
logging.basicConfig(level=logging.INFO, stream=sys.stdout)

# 加载 .env 文件
load_dotenv()

# 获取环境变量
secret_id = os.getenv('COS_SECRET_ID')
secret_key = os.getenv(
    'COS_SECRET_KEY')  # 用户的 SecretKey，建议使用子账号密钥，授权遵循最小权限指引，降低使用风险。子账号密钥获取可参见 https://cloud.tencent.com/document/product/598/37140
region = 'ap-guangzhou'  # 替换为用户的 region，已创建桶归属的 region 可以在控制台查看，https://console.cloud.tencent.com/cos5/bucket
# COS 支持的所有 region 列表参见 https://cloud.tencent.com/document/product/436/6224
token = None  # 如果使用永久密钥不需要填入 token，如果使用临时密钥需要填入，临时密钥生成和使用指引参见 https://cloud.tencent.com/document/product/436/14048
scheme = 'https'  # 指定使用 http/https 协议来访问 COS，默认为 https，可不填

config = CosConfig(Region=region, SecretId=secret_id, SecretKey=secret_key, Token=token, Scheme=scheme)
client = CosS3Client(config)

i18n = I18nAuto()
cut_method_names = get_cut_method_names()

parser = argparse.ArgumentParser(description="GPT-SoVITS api")
parser.add_argument("-c", "--tts_config", type=str, default="GPT_SoVITS/configs/tts_infer.yaml", help="tts_infer路径")
parser.add_argument("-a", "--bind_addr", type=str, default="127.0.0.1", help="default: 127.0.0.1")
parser.add_argument("-p", "--port", type=int, default="9880", help="default: 9880")
args = parser.parse_args()
config_path = args.tts_config
# device = args.device
port = args.port
host = args.bind_addr
argv = sys.argv

if config_path in [None, ""]:
    config_path = "GPT-SoVITS/configs/tts_infer.yaml"

tts_config = TTS_Config(config_path)
print(tts_config)
tts_pipeline = TTS(tts_config)

APP = FastAPI()
APP.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 允许所有来源，生产环境中应指定具体的域名
    allow_credentials=True,  # 允许凭证
    allow_methods=["*"],  # 允许所有 HTTP 方法
    allow_headers=["*"],  # 允许所有头部信息
)
import random
import string

def generate_random_string(length):
    """生成由字母和数字组成的随机字符串"""
    letters_and_digits = string.ascii_letters + string.digits
    return ''.join(random.choice(letters_and_digits) for i in range(length))

class TTS_Request(BaseModel):
    text: str = None
    text_lang: str = None
    ref_audio_path: str = None
    aux_ref_audio_paths: list = None
    prompt_lang: str = None
    prompt_text: str = ""
    top_k: int = 5
    top_p: float = 1
    temperature: float = 1
    text_split_method: str = "cut5"
    batch_size: int = 1
    batch_threshold: float = 0.75
    split_bucket: bool = True
    speed_factor: float = 1.0
    fragment_interval: float = 0.3
    seed: int = -1
    media_type: str = "wav"
    streaming_mode: bool = False
    parallel_infer: bool = True
    repetition_penalty: float = 1.35


### modify from https://github.com/RVC-Boss/GPT-SoVITS/pull/894/files
def pack_ogg(io_buffer: BytesIO, data: np.ndarray, rate: int):
    with sf.SoundFile(io_buffer, mode='w', samplerate=rate, channels=1, format='ogg') as audio_file:
        audio_file.write(data)
    return io_buffer


def pack_raw(io_buffer: BytesIO, data: np.ndarray, rate: int):
    io_buffer.write(data.tobytes())
    return io_buffer


def pack_wav(io_buffer: BytesIO, data: np.ndarray, rate: int):
    io_buffer = BytesIO()
    sf.write(io_buffer, data, rate, format='wav')
    return io_buffer


def pack_aac(io_buffer: BytesIO, data: np.ndarray, rate: int):
    process = subprocess.Popen([
        'ffmpeg',
        '-f', 's16le',  # 输入16位有符号小端整数PCM
        '-ar', str(rate),  # 设置采样率
        '-ac', '1',  # 单声道
        '-i', 'pipe:0',  # 从管道读取输入
        '-c:a', 'aac',  # 音频编码器为AAC
        '-b:a', '192k',  # 比特率
        '-vn',  # 不包含视频
        '-f', 'adts',  # 输出AAC数据流格式
        'pipe:1'  # 将输出写入管道
    ], stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
    out, _ = process.communicate(input=data.tobytes())
    io_buffer.write(out)
    return io_buffer


def pack_audio(io_buffer: BytesIO, data: np.ndarray, rate: int, media_type: str):
    # 将 numpy 数组转换为 pydub 的 AudioSegment 对象
    audio_segment = AudioSegment(
        data.tobytes(),
        frame_rate=rate,
        sample_width=data.dtype.itemsize,  # 确保这是正确的字节宽度
        channels=(1 if len(data.shape) == 1 else data.shape[1])
    )

    if media_type == "ogg":
        # 假设已有 pack_ogg 函数
        io_buffer = pack_ogg(io_buffer, data, rate)
    elif media_type == "aac":
        # 假设已有 pack_aac 函数
        io_buffer = pack_aac(io_buffer, data, rate)
    elif media_type == "wav":
        io_buffer = pack_wav(io_buffer, data, rate)
    elif media_type == "mp3":
        # 使用 pydub 导出为 mp3
        mp3_data = audio_segment.export(format="mp3").read()
        io_buffer.write(mp3_data)
    elif media_type == "webm":
        # 使用 pydub 导出为 webm
        webm_data = audio_segment.export(format="webm").read()
        io_buffer.write(webm_data)
    else:
        io_buffer = pack_raw(io_buffer, data, rate)

    io_buffer.seek(0)
    return io_buffer


# from https://huggingface.co/spaces/coqui/voice-chat-with-mistral/blob/main/app.py
def wave_header_chunk(frame_input=b"", channels=1, sample_width=2, sample_rate=32000):
    # This will create a wave header then append the frame input
    # It should be first on a streaming wav file
    # Other frames better should not have it (else you will hear some artifacts each chunk start)
    wav_buf = BytesIO()
    with wave.open(wav_buf, "wb") as vfout:
        vfout.setnchannels(channels)
        vfout.setsampwidth(sample_width)
        vfout.setframerate(sample_rate)
        vfout.writeframes(frame_input)

    wav_buf.seek(0)
    return wav_buf.read()


def handle_control(command: str):
    if command == "restart":
        os.execl(sys.executable, sys.executable, *argv)
    elif command == "exit":
        os.kill(os.getpid(), signal.SIGTERM)
        exit(0)


def check_params(req: dict):
    text: str = req.get("text", "")
    text_lang: str = req.get("text_lang", "")
    ref_audio_path: str = req.get("ref_audio_path", "")
    streaming_mode: bool = req.get("streaming_mode", False)
    media_type: str = req.get("media_type", "wav")
    prompt_lang: str = req.get("prompt_lang", "")
    text_split_method: str = req.get("text_split_method", "cut5")

    if ref_audio_path in [None, ""]:
        return JSONResponse(status_code=400, content={"message": "ref_audio_path is required"})
    if text in [None, ""]:
        return JSONResponse(status_code=400, content={"message": "text is required"})
    if (text_lang in [None, ""]):
        return JSONResponse(status_code=400, content={"message": "text_lang is required"})
    elif text_lang.lower() not in tts_config.languages:
        return JSONResponse(status_code=400, content={
            "message": f"text_lang: {text_lang} is not supported in version {tts_config.version}"})
    if (prompt_lang in [None, ""]):
        return JSONResponse(status_code=400, content={"message": "prompt_lang is required"})
    elif prompt_lang.lower() not in tts_config.languages:
        return JSONResponse(status_code=400, content={
            "message": f"prompt_lang: {prompt_lang} is not supported in version {tts_config.version}"})
    if media_type not in ["wav", "raw", "ogg", "aac", "mp3", "webm"]:
        return JSONResponse(status_code=400, content={"message": f"media_type: {media_type} is not supported"})
    elif media_type == "ogg" and not streaming_mode:
        return JSONResponse(status_code=400, content={"message": "ogg format is not supported in non-streaming mode"})

    if text_split_method not in cut_method_names:
        return JSONResponse(status_code=400,
                            content={"message": f"text_split_method:{text_split_method} is not supported"})

    return None


async def tts_handle(req: dict):
    """
    Text to speech handler.
    
    Args:
        req (dict): 
            {
                "text": "",                   # str.(required) text to be synthesized
                "text_lang: "",               # str.(required) language of the text to be synthesized
                "ref_audio_path": "",         # str.(required) reference audio path
                "aux_ref_audio_paths": [],    # list.(optional) auxiliary reference audio paths for multi-speaker synthesis
                "prompt_text": "",            # str.(optional) prompt text for the reference audio
                "prompt_lang": "",            # str.(required) language of the prompt text for the reference audio
                "top_k": 5,                   # int. top k sampling
                "top_p": 1,                   # float. top p sampling
                "temperature": 1,             # float. temperature for sampling
                "text_split_method": "cut5",  # str. text split method, see text_segmentation_method.py for details.
                "batch_size": 1,              # int. batch size for inference
                "batch_threshold": 0.75,      # float. threshold for batch splitting.
                "split_bucket: True,          # bool. whether to split the batch into multiple buckets.
                "speed_factor":1.0,           # float. control the speed of the synthesized audio.
                "fragment_interval":0.3,      # float. to control the interval of the audio fragment.
                "seed": -1,                   # int. random seed for reproducibility.
                "media_type": "wav",          # str. media type of the output audio, support "wav", "raw", "ogg", "aac".
                "streaming_mode": False,      # bool. whether to return a streaming response.
                "parallel_infer": True,       # bool.(optional) whether to use parallel inference.
                "repetition_penalty": 1.35    # float.(optional) repetition penalty for T2S model.          
            }
    returns:
        StreamingResponse: audio stream response.
    """

    streaming_mode = req.get("streaming_mode", True)
    return_fragment = req.get("return_fragment", True)
    media_type = req.get("media_type", "wav")

    check_res = check_params(req)
    if check_res is not None:
        return check_res

    if streaming_mode or return_fragment:
        req["return_fragment"] = True

    try:
        tts_generator = tts_pipeline.run(req)

        if streaming_mode:
            def streaming_generator(tts_generator: Generator, media_type: str):
                if media_type == "wav":
                    yield wave_header_chunk()
                    media_type = "raw"
                for sr, chunk in tts_generator:
                    yield pack_audio(BytesIO(), chunk, sr, media_type).getvalue()

            # _media_type = f"audio/{media_type}" if not (streaming_mode and media_type in ["wav", "raw"]) else f"audio/x-{media_type}"
            return StreamingResponse(streaming_generator(tts_generator, media_type, ), media_type=f"audio/{media_type}")

        else:
            sr, audio_data = next(tts_generator)
            audio_data = pack_audio(BytesIO(), audio_data, sr, media_type).getvalue()
            return Response(audio_data, media_type=f"audio/{media_type}")
    except Exception as e:
        return JSONResponse(status_code=400, content={"message": f"tts failed", "Exception": str(e)})


@APP.get("/control")
async def control(command: str = None):
    if command is None:
        return JSONResponse(status_code=400, content={"message": "command is required"})
    handle_control(command)


@APP.get("/tts")
async def tts_get_endpoint(
        text: str = None,
        text_lang: str = None,
        ref_audio_path: str = None,
        aux_ref_audio_paths: list = None,
        prompt_lang: str = None,
        prompt_text: str = "",
        top_k: int = 5,
        top_p: float = 1,
        temperature: float = 1,
        text_split_method: str = "cut0",
        batch_size: int = 1,
        batch_threshold: float = 0.75,
        split_bucket: bool = True,
        speed_factor: float = 1.0,
        fragment_interval: float = 0.3,
        seed: int = -1,
        media_type: str = "wav",
        streaming_mode: bool = False,
        parallel_infer: bool = True,
        repetition_penalty: float = 1.35
):
    file_path = download_file_from_url(ref_audio_path)
    req = {
        "text": text,
        "text_lang": text_lang.lower(),
        "ref_audio_path": file_path,
        "aux_ref_audio_paths": aux_ref_audio_paths,
        "prompt_text": prompt_text,
        "prompt_lang": prompt_lang.lower(),
        "top_k": top_k,
        "top_p": top_p,
        "temperature": temperature,
        "text_split_method": text_split_method,
        "batch_size": int(batch_size),
        "batch_threshold": float(batch_threshold),
        "speed_factor": float(speed_factor),
        "split_bucket": split_bucket,
        "fragment_interval": fragment_interval,
        "seed": seed,
        "media_type": media_type,
        "streaming_mode": streaming_mode,
        "parallel_infer": parallel_infer,
        "repetition_penalty": float(repetition_penalty)
    }
    return await tts_handle(req)


@APP.post("/tts")
async def tts_post_endpoint(request: TTS_Request):
    req = request.dict()
    file_path = download_file_from_url(req.get("ref_audio_path"))
    req["ref_audio_path"] = file_path
    return await tts_handle(req)


@APP.get("/set_refer_audio")
async def set_refer_aduio(refer_audio_path: str = None):
    try:
        tts_pipeline.set_ref_audio(refer_audio_path)
    except Exception as e:
        return JSONResponse(status_code=400, content={"message": f"set refer audio failed", "Exception": str(e)})
    return JSONResponse(status_code=200, content={"message": "success"})


# @APP.post("/set_refer_audio")
# async def set_refer_aduio_post(audio_file: UploadFile = File(...)):
#     try:
#         # 检查文件类型，确保是音频文件
#         if not audio_file.content_type.startswith("audio/"):
#             return JSONResponse(status_code=400, content={"message": "file type is not supported"})

#         os.makedirs("uploaded_audio", exist_ok=True)
#         save_path = os.path.join("uploaded_audio", audio_file.filename)
#         # 保存音频文件到服务器上的一个目录
#         with open(save_path , "wb") as buffer:
#             buffer.write(await audio_file.read())

#         tts_pipeline.set_ref_audio(save_path)
#     except Exception as e:
#         return JSONResponse(status_code=400, content={"message": f"set refer audio failed", "Exception": str(e)})
#     return JSONResponse(status_code=200, content={"message": "success"})

@APP.get("/set_gpt_weights")
async def set_gpt_weights(weights_path: str = None):
    try:
        if weights_path in ["", None]:
            return JSONResponse(status_code=400, content={"message": "gpt weight path is required"})
        tts_pipeline.init_t2s_weights(weights_path)
    except Exception as e:
        return JSONResponse(status_code=400, content={"message": f"change gpt weight failed", "Exception": str(e)})

    return JSONResponse(status_code=200, content={"message": "success"})


@APP.get("/set_sovits_weights")
async def set_sovits_weights(weights_path: str = None):
    try:
        if weights_path in ["", None]:
            return JSONResponse(status_code=400, content={"message": "sovits weight path is required"})
        tts_pipeline.init_vits_weights(weights_path)
    except Exception as e:
        return JSONResponse(status_code=400, content={"message": f"change sovits weight failed", "Exception": str(e)})
    return JSONResponse(status_code=200, content={"message": "success"})


import asyncio
from typing import Dict, Any

p_asr = None  # 全局进程变量
asr_tasks = {}  # 任务状态字典

import os
from typing import Dict, Any
import asyncio
from subprocess import Popen, PIPE


async def asr_RUN(asr_inp_dir: str, asr_opt_dir: str, asr_model: str, asr_model_size: str, asr_lang: str,
                  asr_precision: str) -> Dict[str, Any]:
    global p_asr, task_id
    if p_asr is not None:
        return {"status": "error", "message": "已有任务正在运行"}

    try:
        asr_inp_dir = my_utils.clean_path(asr_inp_dir)
        asr_opt_dir = my_utils.clean_path(asr_opt_dir)
        my_utils.check_for_existance([asr_inp_dir])

        cmd = f'"{python_exec}" tools/asr/{asr_dict[asr_model]["path"]}'
        cmd += f' -i "{asr_inp_dir}"'
        cmd += f' -o "{asr_opt_dir}"'
        cmd += f' -s {asr_model_size}'
        cmd += f' -l {asr_lang}'
        cmd += f" -p {asr_precision}"
        output_file_name = os.path.basename(asr_inp_dir)
        output_folder = asr_opt_dir or "output/asr_opt"
        output_file_path = os.path.abspath(f'{output_folder}/{output_file_name}.list')

        print(cmd)
        p_asr = Popen(cmd, shell=True)
        task_id = str(p_asr.pid)
        asr_tasks[task_id] = {"status": "running", "cmd": cmd, "output_file_path": output_file_path}

        await asyncio.to_thread(p_asr.wait)
        p_asr = None
        asr_tasks[task_id]["status"] = "completed"
        with open(output_file_path, 'r', encoding='utf-8') as file:
            for line in file:
                # 去掉行末的换行符和多余的空白字符
                clean_line = line.strip()
        # 使用高级接口上传一次，不重试，此时没有使用断点续传的功能
        # response = client.upload_file(
        #     Bucket='voice-1325205761',
        #     Key='/public/output_file_name',
        #     LocalFilePath='output_file_path',
        #     EnableMD5=False,
        #     progress_callback=None
        # )

        last_pipe_index = clean_line.rfind('|')

        if last_pipe_index != -1:
            result = clean_line[last_pipe_index + 1:]
        else:
            result = clean_line  # 如果没有找到"|"，则返回原字符串
        return {"status": "success", "text": clean_line}
    except Exception as e:
        asr_tasks[task_id]["status"] = "failed"
        return {"status": "error", "message": str(e)}


@APP.post("/asr")
async def asr(request: Request):
    # try:
    #     json_data = await request.json()
    #     result = await asr_RUN(
    #         json_data.get("input"),
    #         json_data.get("output"),
    #         json_data.get("model"),
    #         json_data.get("modelSize"),
    #         json_data.get("lang"),
    #         json_data.get("precision")
    #     )
    try:
        json_data = await request.json()
        # 保存上传文件到临时目录
        tmp_path = os.path.dirname(download_file_from_url(json_data.get("input")))

        # 调用ASR处理
        result = await asr_RUN(
            tmp_path,
            "D:\\pycode\\GPT-SoVITS-main\\voices",
            json_data.get("model"),
            json_data.get("modelSize"),
            json_data.get("lang"),
            json_data.get("precision")
        )

        # 清理临时文件
        # os.unlink(tmp_path)
        return JSONResponse(result)

    except Exception as e:
        return JSONResponse(
            {"status": "error", "message": str(e)},
            status_code=500
        )


@APP.get("/asr/status/{task_id}")
async def get_asr_status(task_id: str):
    task = asr_tasks.get(task_id)
    if task:
        return task
    return {"status": "error", "message": "任务未找到"}


# 存储所有下载的临时文件路径
temp_files = []


# 下载文件函数
def download_file_from_url(url):
    """
    从指定的URL下载文件，并返回临时文件的路径。
    """
    # 发送HTTP GET请求
    response = requests.get(url, stream=True)
    response.raise_for_status()  # 检查请求是否成功

    # 定义要创建的目录路径
    directory = "tempFile"
    random_str = generate_random_string(10)
    # 获取当前工作目录的路径，并与新目录名组合
    path = os.path.join(os.getcwd(), directory,random_str)

    # 创建目录
    os.makedirs(path, exist_ok=True)  # exist_ok=True 表示如果目录已经存在，则不会引发异常

    print(f"目录 '{path}' 已创建")

    # 假设 'response' 是一个具有 iter_content 方法的对象，比如 requests 的 Response 对象
    # 创建一个临时文件，保存在之前创建的目录中
    with NamedTemporaryFile(dir=path, delete=False) as temp_file:
        for chunk in response.iter_content(chunk_size=8192):
            if chunk:  # 过滤掉保持活动连接的空字节
                temp_file.write(chunk)
    temp_files.append(temp_file.name)
    print(f"文件已保存到 '{temp_file.name}'")

    # 返回临时文件的路径
    return temp_file.name


def delete_old_folders(folder_path, age_in_seconds):
    """删除指定路径下创建时间超过指定秒数的文件夹"""
    current_time = time.time()

    # 遍历指定目录下的所有文件和文件夹
    for root, dirs, files in os.walk(folder_path, topdown=False):
        for name in dirs:
            dir_path = os.path.join(root, name)
            # 获取文件夹的创建时间（在某些操作系统上可能不准确）
            # 使用 stat().st_ctime 获取创建时间或最近元数据更改时间
            creation_time = os.path.getctime(dir_path)

            # 如果文件夹创建于指定秒数之前，则删除它
            if (current_time - creation_time) > age_in_seconds:
                shutil.rmtree(dir_path)  # 删除非空文件夹
                print(f"已删除: {dir_path}")


def monitor_folder(folder_path="/tempFile", interval=60, folder_age=3600):
    """监控指定文件夹并定期清理"""
    while True:
        delete_old_folders(folder_path, folder_age)
        time.sleep(interval)


@APP.post("/download")
async def download(request: Request):
    body = await request.json()
    url = body.get('url')
    if not url:
        raise HTTPException(status_code=400, detail="No URL provided")

    try:
        file_path = download_file_from_url(url)
        temp_files.append(file_path)
        return {"message": "File downloaded successfully", "path": file_path}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to download file. Reason: {str(e)}")


if __name__ == "__main__":
    # 创建并启动监控文件夹的线程
    folder_monitor_thread = threading.Thread(target=monitor_folder,
                                             args=("D:\\pycode\\GPT-SoVITS-main\\tempFile", 60, 3600))
    folder_monitor_thread.daemon = True  # 设置为守护线程，这样当主线程退出时它也会退出
    folder_monitor_thread.start()

    try:
        if host == 'None':  # 在调用时使用 -a None 参数，可以让api监听双栈10.82.35.12910.82.35.129
            host = None
        uvicorn.run(app=APP, host="10.79.178.239", port=9882, workers=1)
    except Exception as e:
        traceback.print_exc()
        os.kill(os.getpid(), signal.SIGTERM)
        exit(0)

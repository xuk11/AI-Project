package com.zbn.springbootinit.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.nls.client.AccessToken;
import com.zbn.springbootinit.API.LongTTSByAli;
import com.zbn.springbootinit.API.TtsWebsocket;
import com.zbn.springbootinit.common.*;
import com.zbn.springbootinit.config.OKHTTPConfig;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.manager.MyWebSocketHandler;
import com.zbn.springbootinit.manager.TTSManager;
import com.zbn.springbootinit.model.dto.tts.TTSRequest;
import com.zbn.springbootinit.model.dto.tts.TTSRequestWithoutType;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

import static com.zbn.springbootinit.controller.PPTController.convert;

/**
 * 语音合成接口
 */
@RestController
@RequestMapping("/tts")
public class TTSController {
    @Resource
    TTSManager ttsManager;
    @Resource
    FileManager fileManager;
    @Resource
    MyWebSocketHandler myWebSocketHandler;
    @Autowired
    private OKHTTPConfig OKHTTPConfig;

    @PostMapping("/fangyan")
    public BaseResponse<String> fangYan(String ttsTextLong, String voiceType) throws IOException {
//        String FilePath =  "/www/wwwroot/output/" + UUID.randomUUID().toString().substring(0, 8) + ".wav";
        String FilePath = "/Users/qwer/Documents/output" + UUID.randomUUID().toString().substring(0, 8) + ".wav";
        String accessKeyId = "LTAI5tJVQSrXjiho8hAhTWMx";
        String accessKeySecret = "9rK71K3JShfUzvKILnKRiC0RXrXuIj";
        AccessToken accessToken = new AccessToken(accessKeyId, accessKeySecret);
        try {
            accessToken.apply();
            System.out.println("Token: " + accessToken.getToken() + ", expire time: " + accessToken.getExpireTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String appKey = "WKXagmiPKXDKNYDW";
        String token = accessToken.getToken();
        // url 默认即可
        String url = "wss://nls-gateway.cn-shanghai.aliyuncs.com/ws/v1";
        LongTTSByAli longTTSByAli = new LongTTSByAli(appKey, token, url, FilePath);
        if (Objects.equals(voiceType, "sichuan")) {
            longTTSByAli.process(ttsTextLong, "xiaoyue");
        } else {
            longTTSByAli.process(ttsTextLong, "aikan");
        }
        longTTSByAli.shutdown();
        return ResultUtils.success(fileManager.uploadFile(convert(FilePath), "tempFile"));
    }

    @PostMapping("/changsha")
    public BaseResponse<String> changsha(String ttsTextLong) throws Exception {
        TtsWebsocket ttsWebsocket = new TtsWebsocket();
        ttsWebsocket.process(ttsTextLong);
        return ResultUtils.success(fileManager.uploadFile(convert("/www/wwwroot/output/" + "test.mp3"), "tempFile"));
    }

    @PostMapping
    public String tts(@org.springframework.web.bind.annotation.RequestBody TTSRequest ttsRequest) {
        if (ttsRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        TTSRequestWithoutType ttsRequestWithoutType = new TTSRequestWithoutType();
        BeanUtils.copyProperties(ttsRequest, ttsRequestWithoutType);
        // 合成音频文件
        if (ttsRequest.getType() == null) {
            return ttsManager.postTTs(ttsRequestWithoutType, -1);
        }
        return ttsManager.postTTs(ttsRequestWithoutType, ttsRequest.getType());
    }


    @PostMapping("/custom")
    public String ttsByCustom(@org.springframework.web.bind.annotation.RequestBody TTSRequest ttsRequest) throws IOException {
        if (ttsRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        TTSRequestWithoutType ttsRequestWithoutType = new TTSRequestWithoutType();
        BeanUtils.copyProperties(ttsRequest, ttsRequestWithoutType);
        if (ttsRequest.getRef_audio_path() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请上传音频文件");
        }
        ttsRequestWithoutType.setRef_audio_path(fileManager.uploadFile(convert(ttsRequest.getRef_audio_path()), "tempFile"));
        // 合成音频文件
        return ttsManager.postTTs(ttsRequestWithoutType, 5);
    }

    //    @PostMapping("/qwen")
//    public BaseResponse<Boolean> qwen(String message) {
//        QwenRequest qwenRequest = new QwenRequest(message);
//        String response = HttpUtil.createPost("http://localhost:11434/api/generate").body(JSONUtil.toJsonStr(qwenRequest)).execute().body();
//        JSONObject entries = JSONUtil.parseObj(response);
//        String text = entries.getStr("text");
//        myWebSocketHandler.broadcast(new ProgressMessage(0, text, "success", "qwen"));
//        return ResultUtils.success(true);
//    }

    @PostMapping("/qwen")
    public BaseResponse<Boolean> qwen(@RequestParam String message) {
        QwenRequest qwenRequest = new QwenRequest(message);
        String requestJson = JSONUtil.toJsonStr(qwenRequest);

        // 修正后的请求体创建
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(requestJson, mediaType);

        Request request = new Request.Builder()
                .url("http://localhost:11434/api/generate")
                .post(body)
                .build();
        OkHttpClient okHttpClient = OKHTTPConfig.okHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    BufferedSource source = responseBody.source();
                    while (!source.exhausted()) {
                        String chunk = source.readUtf8Line(); // 假设服务端以换行分隔每个JSON块
                        if (chunk != null) {
                            JSONObject json = JSONUtil.parseObj(chunk);
                            String text = json.getStr("response");
                            System.out.println("Received chunk: " + text);
                            myWebSocketHandler.broadcast(new ProgressMessage(0, text, "success", "qwen"));
                        }
                    }
                } catch (Exception e) {
                    myWebSocketHandler.broadcast(new ProgressMessage(0, "", "error", "qwen"));
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                myWebSocketHandler.broadcast(new ProgressMessage(0, "", "error", "qwen"));
            }
        });

        return ResultUtils.success(true);
    }
}

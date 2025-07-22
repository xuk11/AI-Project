package com.zbn.springbootinit.manager;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zbn.springbootinit.constant.CommonConstant;
import com.zbn.springbootinit.constant.FileConstant;
import com.zbn.springbootinit.model.dto.tts.TTSRequestWithoutType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import static com.zbn.springbootinit.API.HttpAudioRequest.saveAudioToFile;

@Service
@Slf4j
public class TTSManager {
    @Resource
    CosManager cosManager;
    String Text;
    String audioUrl;

    public void ChooseModel(int type) {
        String GPT_weights = "GPT_weights_v2/kh-e25.ckpt";
        String SoVITS_weights = "SoVITS_weights_v2/kh_e32_s384.pth";
        switch (type) {
            case 1:
                GPT_weights = "GPT_weights_v2/kh-e25.ckpt";
                SoVITS_weights = "SoVITS_weights_v2/kh_e32_s384.pth";
                Text = "自小爱红楼的我，自然也极爱着正定古城里的这座赤造荣国府。";
                audioUrl = "/root/code/GPT-SoVITS-main/voices/special1_0.wav";
                break;
            case 2:
                GPT_weights = "GPT_weights_v2/man-e15.ckpt";
                SoVITS_weights = "SoVITS_weights_v2/man_e24_s264.pth";
                Text = "多么严密的结构哇，宗谱先生真不愧是写文章的好手";
                audioUrl = "https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public/audio/PresetVoice/Mandarin/man2.wav";
                // audioUrl = "https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fman2.wav";
                //audioUrl = "voices/split2_[cut_6sec].wav";
                break;
            case 3:
                GPT_weights = "GPT_weights_v2/woman-e45.ckpt";
                SoVITS_weights = "SoVITS_weights_v2/woman_e8_s96.pth";
                Text = "物品的价值和人的价值是一样的吗";
                audioUrl = "https://voice-1325205761.cos.ap-guangzhou.myqcloud.com/public%2Faudio%2FPresetVoice%2FMandarin%2Fwomen.wav";
                //audioUrl = "voices/audio_0002_16_[cut_5sec].wav";
                break;
            case 4:
                GPT_weights = "GPT_weights_v2/olj-e45.ckpt";
                SoVITS_weights = "SoVITS_weights_v2/woman_e8_s96.pth";
                Text = "只要有这么一点点快乐的回忆，它可以永劫轮回，都在所不惜";
                audioUrl = "/root/code/GPT-SoVITS-main/voices/olj.wav";
                // audioUrl = "voices/audio1_0001_17_[cut_5sec] (1).wav";
                break;
            case 5:
                GPT_weights = "GPT_weights_v2/zbn-e35.ckpt";
                SoVITS_weights = "SoVITS_weights_v2/zbn_e48_s768.pth";
                break;
            default:
                GPT_weights = "GPT_weights_v2/s1bert25hz-5kh-longer-epoch=12-step=369668.ckpt";
                SoVITS_weights = "SoVITS_weights_v2/s2G2333k.pth";
                log.info("没有选择预设音色");
        }
        String url = String.format("http://i-2.gpushare.com:37210/set_sovits_weights?weights_path=%s", SoVITS_weights);
        HttpUtil.createGet(url).execute().body();
        url = String.format("http://i-2.gpushare.com:37210/set_gpt_weights?weights_path=%s", GPT_weights);
        HttpUtil.createGet(url).execute().body();
    }

    public String tts(String text, String language, int type) {
        ChooseModel(type);
        // 生成请求地址
        String url = "http://i-2.gpushare.com:37210/tts?text=" +
                text +
                "&text_lang=" +
                language +
                "&ref_audio_path=" +
                audioUrl +
                "&prompt_lang=zh" + "&prompt_text=" +
                Text +
                "&text_split_method=" +
                "cut0" +
                "&batch_size=" +
                "50" +
                "&media_type=" +
                "wav" +
                "&streaming_mode=" +
                "false";

        // 定义目标文件路径
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        String dateStr = year + "-" + month + "-" + day;
        String uuid = RandomUtil.randomString(16);
        String filename = String.format("%s_%s_%s-%s.wav", dateStr, type, language, uuid);
        String filePath = String.format("/www/wwwroot/output/%s", filename);
        // 从网络获取音频流
        try (InputStream inputStream = HttpUtil.createGet(url).execute().bodyStream()) {
            // 检查音频流是否有效
            if (inputStream == null) {
                System.out.println("Failed to download the audio file.");
                return "";
            }
            saveAudioToFile(inputStream, filePath);
        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
        }
        return filePath;
    }

    public String postTTs(String text, String language, int type) {
        TTSRequestWithoutType ttsRequestWithoutType = new TTSRequestWithoutType(text, language);
        return postTTs(ttsRequestWithoutType, type);
    }

    public String postTTs(TTSRequestWithoutType ttsRequestWithoutType, int type) {
        String url = "http://i-2.gpushare.com:37210/tts";
        if (type != -1) {
            ChooseModel(type);
            if (type != 5) {
                ttsRequestWithoutType.setPrompt_text(Text);
                ttsRequestWithoutType.setRef_audio_path(audioUrl);
            }
        }
        String createVideoStr = JSONUtil.toJsonStr(ttsRequestWithoutType);
        // 定义目标文件路径
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        String dateStr = year + "-" + month + "-" + day;
        String uuid = RandomUtil.randomString(16);
        String filename = String.format("%s_%s_%s-%s.wav", dateStr, type, ttsRequestWithoutType.getText_lang(), uuid);
        String filePath = String.format(CommonConstant.LOCAL_FILE_PATH, filename);
        // 从网络获取音频流
        try (InputStream inputStream = HttpUtil.createPost(url).body(createVideoStr).execute().bodyStream()) {
            // 检查音频流是否有效
            if (inputStream == null) {
                System.out.println("Failed to download the audio file.");
                return "";
            }
            saveAudioToFile(inputStream, filePath);
        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
        }
        if (type == -1) {
            File localFile = new File(filePath);
            String filepath = String.format("/tempFile/%s", filename);
            cosManager.putObject(filepath, localFile);
            filePath = FileConstant.COS_HOST + "/tempFile" + "%2F" + filename;
        }
        return filePath;
    }
}

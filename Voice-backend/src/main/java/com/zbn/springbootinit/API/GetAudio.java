package com.zbn.springbootinit.API;

import cn.hutool.http.HttpUtil;

import java.io.IOException;
import java.io.InputStream;

import static com.zbn.springbootinit.API.HttpAudioRequest.saveAudioToFile;

public class GetAudio {
    public static String tts(String text, String language) {
        String url = "http://i-2.gpushare.com:37210/tts?text=" +
                text +
                "&text_lang=" +
                language +
                "&ref_audio_path=D:\\Downloads\\special1_0.wav&prompt_lang=zh&prompt_text=自小爱红楼的我，自然也极爱着正定古城里的这座赤造荣国府。&text_split_method=cut0&batch_size=50&media_type=wav&streaming_mode=false";

        // 定义目标文件路径
        String filePath = "/www/wwwroot/output/02.wav";
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

    public static void main(String[] args) {
        tts("智言教合，一个汇聚智慧与言辞精粹的教育合作平台，致力于打造一个开放、互动的学习社群。", "yue");
    }
}
package com.zbn.springbootinit.API;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpUtil;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpAudioRequest {

    public static void getAudioFile(String urlString, String destinationFilePath) {
        HttpURLConnection urlConnection = null;
        try {
            // 创建URL对象
            URL url = new URL(urlString);
            // 打开连接
            urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求方法为GET
            urlConnection.setRequestMethod("GET");

            // 检查响应码是否为HTTP_OK（200）
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 获取输入流
                InputStream inputStream = urlConnection.getInputStream();

                // 写入文件
                FileOutputStream fileOutputStream = new FileOutputStream(destinationFilePath);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
                fileOutputStream.close();
                inputStream.close();
                System.out.println("文件下载成功！");
            } else {
                System.out.println("请求失败，状态码：" + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        String modelPath = "SoVITS_weights_v2/kh_e32_s384.pth";
        String url = String.format("http://i-2.gpushare.com:37210/set_sovits_weights?weights_path=%s", modelPath);
        HttpUtil.createGet(url).execute().body();
        url = "http://i-2.gpushare.com:37210/tts?text=先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。&text_lang=zh&ref_audio_path=D:\\Downloads\\special1_0.wav&prompt_lang=zh&prompt_text=自小爱红楼的我，自然也极爱着正定古城里的这座赤造荣国府。&text_split_method=cut0&batch_size=50&media_type=wav&streaming_mode=false";

        // 定义目标文件路径
        String filePath = "/www/wwwroot/output/01.wav";
// 从网络获取音频流
        try (InputStream inputStream = HttpUtil.createGet(url).execute().bodyStream()) {
            // 检查音频流是否有效
            if (inputStream == null) {
                System.out.println("Failed to download the audio file.");
                return;
            }
            saveAudioToFile(inputStream, filePath);
        } catch (IOException e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 将音频流保存为文件
    public static void saveAudioToFile(InputStream inputStream, String outputPath) {
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            IoUtil.copy(inputStream, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
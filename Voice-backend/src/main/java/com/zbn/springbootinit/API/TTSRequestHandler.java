package com.zbn.springbootinit.API;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TTSRequestHandler {

    public static void main(String[] args) {
        // 创建HttpClient实例
        HttpClient client = HttpClient.newHttpClient();

        // 构造请求体，JSON格式
        String requestBody = "{"
                + "\"text\": \"先帝创业未半而中道崩殂，今天下三分，益州疲弊，此诚危急存亡之秋也。\", "
                + "\"text_lang\": \"zh\", "
                + "\"ref_audio_path\": \"D:\\Downloads\\special1_0.wav\", "
                + "\"media_type\": \"wav\", "
                + "\"streaming_mode\": false"
                + "}";

        // 创建HttpRequest对象
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://i-2.gpushare.com:37210/tts")) // 替换为你的服务器地址和端口
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        try {
            // 发送请求并接收响应
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

            // 检查状态码是否为200（OK）
            if (response.statusCode() == 200) {
                // 定义输出路径和文件名
                String outputPath = "output_audio.wav"; // 你想要保存的文件路径

                // 将输入流写入到文件
                Files.copy(response.body(), Paths.get(outputPath));
                System.out.println("Audio file saved successfully at: " + outputPath);
            } else {
                System.err.println("Failed to get audio file. Status code: " + response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
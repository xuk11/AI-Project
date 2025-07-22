package com.zbn.springbootinit.API;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FastAPIClient {

    private static final String POST_URL = "http://i-2.gpushare.com:37210/generate_video";
    private static final Logger log = LoggerFactory.getLogger(FastAPIClient.class);

    public static List<String> PostPycharm(String jsonInputString) throws Exception {
        List<String> resultList = new ArrayList<>();
        HttpURLConnection httpConn = getHttpURLConnection(jsonInputString);
        int responseCode = httpConn.getResponseCode();
        log.info("Response Code : {}", responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) { // success
            // 读取响应内容
            try (BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // 解析响应内容为 JSON 数组
                JSONArray jsonResponse = new JSONArray(response.toString());
                System.out.println("Response Body: " + jsonResponse);
                // 处理返回的数据
                for (int i = 0; i < jsonResponse.length(); i++) {
                    String result = jsonResponse.getString(i);
                    System.out.println("Result " + i + ": " + result);
                    resultList.add(result);
                }
            }
        } else {
            System.out.println("POST request not worked");
        }
        httpConn.disconnect();
        return resultList;
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(String jsonInputString) throws IOException {
        URL url = new URL(POST_URL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setUseCaches(false);
        httpConn.setDoOutput(true);
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("Content-Type", "application/json; utf-8");
        httpConn.setRequestProperty("Accept", "application/json");
        try (OutputStream os = httpConn.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return httpConn;
    }
}
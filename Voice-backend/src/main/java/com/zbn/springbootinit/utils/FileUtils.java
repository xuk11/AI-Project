package com.zbn.springbootinit.utils;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;

@Component
public class FileUtils {
    public String getFileChecksum(MessageDigest digest, File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);

        byte[] byteArray = new byte[1024];
        int bytesCount;
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }
        fis.close();
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public MultipartFile convert(String filePath) throws IOException {
        // 创建一个File对象指向你的文件
        File file = new File(filePath);

        // 确保文件存在
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }

        // 获取文件输入流
        InputStream inputStream = new FileInputStream(file);

        // 使用MockMultipartFile构造函数创建MultipartFile对象
        return new MockMultipartFile(file.getName(),
                file.getName(),
                "application/octet-stream",
                inputStream);
    }
}

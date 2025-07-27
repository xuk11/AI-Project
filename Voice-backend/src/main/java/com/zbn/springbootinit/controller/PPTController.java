package com.zbn.springbootinit.controller;

import com.zbn.springbootinit.common.*;
import com.zbn.springbootinit.config.CosClientConfig;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.manager.MyWebSocketHandler;
import com.zbn.springbootinit.model.dto.ppt.PPTConvertRequest;
import com.zbn.springbootinit.model.entity.Auditoryppt;
import com.zbn.springbootinit.service.AuditorypptService;
import com.zbn.springbootinit.service.PPTService;
import com.zbn.springbootinit.service.UserService;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.time.LocalDate;

@RestController
@RequestMapping("/ppt")
public class PPTController {

    @Resource
    private PPTService pptService;
    @Resource
    private FileManager fileManager;
    @Resource
    private UserService userService;
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    AuditorypptService auditorypptService;
    @Resource
    MyWebSocketHandler myWebSocketHandler;

    @PostMapping("/convert")
    public BaseResponse<String> convertPPT(@RequestBody PPTConvertRequest pptConvertRequest, HttpServletRequest request) {
        Long userId = userService.getLoginUser(request).getId();
        try {
            ProgressMessage progressMessage = new ProgressMessage(0, "", "progress", "ppt");
            progressMessage.setProgress(10);
            myWebSocketHandler.broadcast(progressMessage);
            GenerateCaptionsResult result = pptService.convertPPT(pptConvertRequest.getImageList(), pptConvertRequest.getType(), pptConvertRequest.getFileHash());
            progressMessage.setProgress(95);
            progressMessage.setMessage("生成完成，正在上传云端.....");
            myWebSocketHandler.broadcast(progressMessage);
            LocalDate date = LocalDate.now();
            int year = date.getYear();
            int month = date.getMonthValue();
            int day = date.getDayOfMonth();
            String dateStr = year + "-" + month + "-" + day;
            Auditoryppt auditoryppt = new Auditoryppt();
            auditoryppt.setName(dateStr + "有声PPT");
            auditoryppt.setFilePath(result.getFilePath());
            auditoryppt.setFileSize(result.getFileSize());
            auditoryppt.setFileType("video/mp4");
            auditoryppt.setUserId(userId);
            auditoryppt.setDuration(result.getDuration());
            progressMessage.setProgress(99);
            progressMessage.setMessage("上传完成，正在保存至个人中心.....");
            myWebSocketHandler.broadcast(progressMessage);
            auditorypptService.save(auditoryppt);
            progressMessage.setProgress(100);
            progressMessage.setMessage("已完成");
            progressMessage.setStatus("completed");
            myWebSocketHandler.broadcast(progressMessage);
            return ResultUtils.success(auditoryppt.getFilePath());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    public static MultipartFile convert(String filePath) throws IOException {
        // 创建一个File对象指向你的文件
        File file = new File(filePath);

        // 确保文件存在
        if (!file.exists()) {
            throw new IllegalArgumentException("文件不存在: " + filePath);
        }

        // 获取文件输入流
        InputStream inputStream = new FileInputStream(file);

        // 使用MockMultipartFile构造函数创建MultipartFile对象
        return new MockMultipartFile(file.getName(), file.getName(), "application/octet-stream", inputStream);
    }

    public static String getFileChecksum(MessageDigest digest, MultipartFile file) throws IOException {
        try (InputStream fis = file.getInputStream()) {
            byte[] byteArray = new byte[1024];
            int bytesCount;
            // 读取文件的字节并更新摘要
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        // 获取文件摘要（哈希值）
        byte[] bytes = digest.digest();

        // 将摘要转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
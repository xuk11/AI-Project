package com.zbn.springbootinit.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaFormat;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaInfoRequest;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaInfoResponse;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaInfoStreamObject;
import com.zbn.springbootinit.common.ErrorCode;
import com.zbn.springbootinit.common.ProgressMessage;
import com.zbn.springbootinit.common.VideoResetMessage;
import com.zbn.springbootinit.config.CosClientConfig;
import com.zbn.springbootinit.constant.ServiceURL;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.manager.MyWebSocketHandler;
import com.zbn.springbootinit.mapper.VideoMapper;
import com.zbn.springbootinit.model.entity.Video;
import com.zbn.springbootinit.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * @author qwer
 * @description 针对表【video(视频)】的数据库操作Service实现
 * @createDate 2025-02-28 19:16:05
 */
@Service
@Slf4j
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements VideoService {
    @Resource
    private FileManager fileManager;
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    MyWebSocketHandler myWebSocketHandler;
    private static final int MAX_RETRIES = 3;
    private static final long BASE_DELAY_MS = 1000L;
    private static final int TIME_OUT = 180000;

    @Override
    public String uploadVideo(MultipartFile file, String uploadPathPrefix, Long userId) {
        String uploadPath = fileManager.uploadVideo(file, uploadPathPrefix);
        // 创建媒体信息请求对象
        MediaInfoRequest mediaInfoRequest = new MediaInfoRequest();
        mediaInfoRequest.setBucketName(cosClientConfig.getBucket());
        mediaInfoRequest.getInput().setObject(uploadPath);
        MediaInfoResponse response = cosClientConfig.cosClient().generateMediainfo(mediaInfoRequest);
        // 从MediaInfoResponse中获取所需信息
        MediaInfoStreamObject mediaInfo = response.getMediaInfo();
        MediaFormat format = mediaInfo.getFormat();

        String fileSize = format.getSize(); // 文件大小
        String duration = format.getDuration(); // 视频时长
        String filePath = cosClientConfig.getHost() + "/" + uploadPath.substring(1).replace("/", "%2F");
        Video video = new Video();
        video.setName("Test");
        video.setFilePath(filePath);
        video.setFileSize(Long.valueOf(fileSize));
        video.setFileType("video/mp4");
        video.setUserId(userId);
        video.setDuration(duration);
        this.save(video);
        return filePath;
    }


    @Override
    public String processVideoSynthesis(ProgressMessage message, VideoResetMessage videoResetMessage) throws InterruptedException {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try (HttpResponse response = HttpUtil.createPost(ServiceURL.VIDEO_SYNTHESIS)
                    .timeout(TIME_OUT)
                    .body(JSONUtil.toJsonStr(videoResetMessage))
                    .execute()) {
                // 增强校验
                if (!response.isOk()) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "内部服务请求异常");
                }
                List<String> list = JSONUtil.toList(response.body(), String.class);
                updateProgress(message, 70, 90, "视频合成进度");
                return list.get(0);
            } catch (Exception e) { // 扩大异常捕获范围
                retryCount++;
                log.warn("视频合成请求异常 (重试 {}/{}), 原因: {}", retryCount, MAX_RETRIES, e.getMessage());

                if (retryCount == MAX_RETRIES) {
                    handleProgressError(message, "视频合成服务异常");
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "合成服务失败");
                }

                long delay = (long) (BASE_DELAY_MS * Math.pow(2, retryCount));
                sleep(delay);
            }
        }
        return null;
    }

    @Override
    public void handleProgressError(ProgressMessage message, String errorMsg) {
        message.setProgress(0);
        message.setMessage(errorMsg);
        message.setStatus("error");
        myWebSocketHandler.broadcast(message);
    }

    @Override
    public void updateProgress(ProgressMessage msg, int start, int end, String message) {
        msg.setProgress(end);
        msg.setMessage(message);
        myWebSocketHandler.broadcast(msg);
    }
}

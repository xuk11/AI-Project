package com.zbn.springbootinit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbn.springbootinit.common.ProgressMessage;
import com.zbn.springbootinit.common.VideoResetMessage;
import com.zbn.springbootinit.model.entity.Video;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qwer
 * @description 针对表【video(视频)】的数据库操作Service
 * @createDate 2025-02-28 19:16:05
 */
public interface VideoService extends IService<Video> {

    String uploadVideo(MultipartFile file, String uploadPathPrefix, Long userId);

    // 视频合成处理（含重试机制）
    String processVideoSynthesis(ProgressMessage message, VideoResetMessage videoResetMessage) throws InterruptedException;

    void handleProgressError(ProgressMessage message, String errorMsg);

    void updateProgress(ProgressMessage msg, int start, int end, String message) throws InterruptedException;
}

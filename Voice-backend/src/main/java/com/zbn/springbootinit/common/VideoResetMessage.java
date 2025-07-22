package com.zbn.springbootinit.common;

import lombok.Data;

/**
 * 视频置换
 *
 * @author zbn
 * @date 2023/8/1
 */
@Data
public class VideoResetMessage {
    /**
     * 从视频中分离出来的音频文件路径
     */
    private String filePath;
    /**
     * 任务ID
     */
    private String taskId;
    /**
     * 视频文件hash
     */
    private String hash;
}

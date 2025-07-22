package com.zbn.springbootinit.model.dto.audioCommunity;

import lombok.Data;

@Data
public class UpdateAudio {
    /**
     * 音频文件唯一ID
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 是否分享
     */
    private Integer isShare;
}

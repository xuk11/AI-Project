package com.zbn.springbootinit.model.dto.ppt;

import lombok.Data;

import java.util.ArrayList;

/**
 * 请求生成视频
 */
@Data
public class CreateVideoRequest {
    /**
     * 图片集合
     */
    ArrayList<String> imageList;

    /**
     * 声音集合
     */
    ArrayList<String> audioList;
}

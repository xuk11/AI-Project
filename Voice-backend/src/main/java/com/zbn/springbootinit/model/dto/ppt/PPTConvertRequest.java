package com.zbn.springbootinit.model.dto.ppt;

import lombok.Data;

import java.util.ArrayList;

/**
 * 有声PPT请求类
 */
@Data
public class PPTConvertRequest {
    /**
     * PPT图片URL列表
     */
    ArrayList<String> imageList;
    /**
     * 预设声音类型
     */
    int type;
    /**
     * 文件哈希值
     */
    String fileHash;
}

package com.zbn.springbootinit.model.dto.Auditoryppt;

import com.zbn.springbootinit.common.PageRequest;
import lombok.Data;

@Data
public class AuditorypptQueryRequest extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     * 文件名
     */
    private String name;

    /**
     * 存储地址
     */
    private String filePath;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 个人中心ID
     */
    private Long spaceId;

    /**
     * 音色ID
     */
    private Long audioId;
}

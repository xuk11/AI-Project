package com.zbn.springbootinit.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 文件上传请求
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 * @date 2024/06/23
 */
@Data
public class UploadFileRequest implements Serializable {

    /**
     * 业务
     */
    private String biz;

    /**
     * 文件名称
     */
    private String title;
    /**
     * 是否共享
     */
    private int isShare;

    private static final long serialVersionUID = 1L;
}
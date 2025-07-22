package com.zbn.springbootinit.common;

import lombok.Data;

@Data
public class GenerateCaptionsResult {
    private String filePath;
    private Long fileSize;
    private String duration;
}

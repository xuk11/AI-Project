package com.zbn.springbootinit.model.dto.video;

import lombok.Data;

@Data
public class VideoRequest {
    String filePath;
    int type;
    String language;
}

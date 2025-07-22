package com.zbn.springbootinit.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GenerateCaptionsRequest {
    String taskId;
    String modelSize;
    String language;
    String videoPath;

    public GenerateCaptionsRequest(String taskId, String videoPath, String language) {
        this.taskId = taskId;
        this.modelSize = "medium";
        this.language = language;
        this.videoPath = videoPath;
    }
}

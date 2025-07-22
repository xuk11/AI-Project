package com.zbn.springbootinit.model.dto.ppt;

import lombok.Data;

@Data
public class PreviewResponse {
    private String[] slides;
    private String error;
    private boolean success;

    public PreviewResponse(String[] slides) {
        this.slides = slides;
        this.success = true;
    }

    public PreviewResponse(String[] slides, String error) {
        this.slides = slides;
        this.error = error;
        this.success = false;
    }
}
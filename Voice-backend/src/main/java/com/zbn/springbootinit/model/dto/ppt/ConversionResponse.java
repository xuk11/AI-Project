package com.zbn.springbootinit.model.dto.ppt;

import lombok.Data;

@Data
public class ConversionResponse {
    private String downloadUrl;
    private String error;
    private boolean success;

    public ConversionResponse(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        this.success = true;
    }

    public ConversionResponse(String downloadUrl, String error) {
        this.downloadUrl = downloadUrl;
        this.error = error;
        this.success = false;
    }
}
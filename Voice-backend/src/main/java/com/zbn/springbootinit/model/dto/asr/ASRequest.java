package com.zbn.springbootinit.model.dto.asr;

import lombok.Data;

@Data
public class ASRequest {
    private String input;

    private String model;

    private String modelSize;

    private String lang;

    private String precision;
}

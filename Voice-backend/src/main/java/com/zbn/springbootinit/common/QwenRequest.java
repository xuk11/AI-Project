package com.zbn.springbootinit.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class QwenRequest {
    String model = "qwen2.5-coder:14b";
    String prompt;
    Boolean stream = true;

    public QwenRequest(String prompt) {
        this.prompt = prompt;
    }
}

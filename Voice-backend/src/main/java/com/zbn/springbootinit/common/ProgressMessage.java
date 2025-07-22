package com.zbn.springbootinit.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProgressMessage {
    private int progress;
    private String message;
    private String status;
    private String task;
}

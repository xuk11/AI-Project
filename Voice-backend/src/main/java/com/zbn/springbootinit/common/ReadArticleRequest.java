package com.zbn.springbootinit.common;

import lombok.Data;

@Data
public class ReadArticleRequest {
    private String text;
    private int type;
}

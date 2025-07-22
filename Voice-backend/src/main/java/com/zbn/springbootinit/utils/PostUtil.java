package com.zbn.springbootinit.utils;

import cn.hutool.http.HttpUtil;
import org.springframework.stereotype.Component;

@Component
public class PostUtil {
    public String getPostResponse(String url, String JSONStr) {
        return HttpUtil.createPost(url).body(JSONStr).execute().body();
    }
}

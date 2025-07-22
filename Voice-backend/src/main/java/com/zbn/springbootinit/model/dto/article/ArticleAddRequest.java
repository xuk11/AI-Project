package com.zbn.springbootinit.model.dto.article;

import lombok.Data;

import java.util.List;

/**
 * 新增书籍
 *
 * @author zbn
 */

@Data
public class ArticleAddRequest {
    /**
     * 标题
     */
    private String title;
    /**
     * 概要
     */
    private String summary;
    /**
     * 作者
     */
    private String author;
    /**
     * 内容
     */
    private String content;
    /**
     * 封面图片
     */
    private String coverImage;
    /**
     * 内容图片
     */
    private List<String> contentImages;
}

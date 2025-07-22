package com.zbn.springbootinit.model.dto.article;

import com.zbn.springbootinit.common.PageRequest;
import lombok.Data;

import java.util.List;

@Data
public class ArticleQueryRequest extends PageRequest {
    /**
     * id
     */
    private Long id;
    /**
     * 摘要
     */
    private List<String> summary;
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private List<String> content;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签(JSON数组)
     */
    private List<String> tags;

    /**
     * 封面图片
     */
    private String coverImage;

    /**
     * 阅读量
     */
    private Long viewsCount;

    /**
     * 点赞量
     */
    private Long likesCount;

    /**
     * SEO标题
     */
    private String SEOTitle;

    /**
     * SEO描述
     */
    private String SEODescription;

    /**
     * SEO关键字
     */
    private String SEOKeywords;

    /**
     * 优先级
     */
    private Integer priority;
    private String searchText;
    private Long userId;
}

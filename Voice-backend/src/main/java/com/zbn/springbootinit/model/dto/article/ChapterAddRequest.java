package com.zbn.springbootinit.model.dto.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChapterAddRequest implements Serializable {
    /**
     * 书本ID（关联article表）
     */
    private Long articleId;

    /**
     * 章节标题
     */
    private String chapterTitle;

    /**
     * 章节内容
     */
    private String content;

    /**
     * 章节顺序（从1开始）
     */
    private Integer chapterOrder;

    /**
     * 本章字数
     */
    private Long wordCount;

    /**
     * 是否免费章节（0-付费，1-免费）
     */
    private Integer isFree;

    @Serial
    private static final long serialVersionUID = 1L;
}
package com.zbn.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 书本章节表
 *
 * @TableName Chapter
 */
@TableName(value = "Chapter")
@Data
public class Chapter {
    /**
     * 章节ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;
}
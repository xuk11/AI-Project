package com.zbn.springbootinit.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 视频
 *
 * @TableName Article
 */
@TableName(value = "Article")
@Data
public class Article {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * 摘要
     */
    private String summary;
    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 作者
     */
    private String author;

    /**
     * 标签(JSON数组)
     */
    private String tags;

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

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 总字数
     */
    private Long countSize;

    /**
     * 总章节数
     */
    private Integer categoryCount;

    /**
     * 1-散文  2-小说
     */
    private Integer type;

    /**
     * 描述
     */
    private String description;

    /**
     * 0 - 草稿 1 - 已发布
     */
    private Integer status;

    /**
     *
     */
    private String category;
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
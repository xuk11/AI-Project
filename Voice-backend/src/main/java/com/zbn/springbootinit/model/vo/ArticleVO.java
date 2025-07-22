package com.zbn.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zbn.springbootinit.model.entity.Article;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

/**
 * 文章
 *
 * @TableName Article
 */
@TableName(value = "Article")
@Data
public class ArticleVO {
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

    private UserVO userVO;

    public static ArticleVO objToVo(Article article) {
        if (article == null) return null;
        article.setContent(article.getContent().replaceAll("\\s", ""));
        article.setSummary(article.getSummary().replaceAll("\\s", ""));
        ArticleVO articleVO = new ArticleVO();
        BeanUtils.copyProperties(article, articleVO);
        if (article.getTags() != null) {
            articleVO.setTags(JSONUtil.toList(article.getTags(), String.class));
        }
        if (article.getContent() != null) {
            articleVO.setContent(JSONUtil.toList(article.getContent(), String.class));
        }
        if (article.getSummary() != null) {
            articleVO.setSummary(JSONUtil.toList(article.getSummary(), String.class));
        }
        return articleVO;
    }
}
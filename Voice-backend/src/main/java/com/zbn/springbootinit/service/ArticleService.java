package com.zbn.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbn.springbootinit.model.dto.article.ArticleAddRequest;
import com.zbn.springbootinit.model.dto.article.ArticleQueryRequest;
import com.zbn.springbootinit.model.entity.Article;
import com.zbn.springbootinit.model.vo.ArticleVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qwer
 * @description 针对表【Article(视频)】的数据库操作Service
 * @createDate 2025-03-18 23:39:20
 */
public interface ArticleService extends IService<Article> {
    /**
     * 校验数据
     *
     * @param articleAddRequest
     */
    void validateArticle(ArticleAddRequest articleAddRequest);

    /**
     * 获取查询条件
     *
     * @param articleQueryRequest
     * @return
     */
    QueryWrapper<Article> getQueryWrapper(ArticleQueryRequest articleQueryRequest);

    /**
     * 获取文章封装
     *
     * @param article
     * @param request
     * @return
     */
    ArticleVO getArticleVO(Article article, HttpServletRequest request);

    /**
     * 分页获取音频文件封装
     *
     * @param articlePage
     * @param request
     * @return
     */
    Page<ArticleVO> getArticleVOPage(Page<Article> articlePage, HttpServletRequest request);
}

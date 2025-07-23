package com.zbn.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.common.ErrorCode;
import com.zbn.springbootinit.constant.CommonConstant;
import com.zbn.springbootinit.exception.ThrowUtils;
import com.zbn.springbootinit.mapper.ArticleMapper;
import com.zbn.springbootinit.model.dto.article.ArticleAddRequest;
import com.zbn.springbootinit.model.dto.article.ArticleQueryRequest;
import com.zbn.springbootinit.model.entity.Article;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.ArticleVO;
import com.zbn.springbootinit.model.vo.UserVO;
import com.zbn.springbootinit.service.ArticleService;
import com.zbn.springbootinit.service.UserService;
import com.zbn.springbootinit.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author qwer
 * @description 针对表【Article(视频)】的数据库操作Service实现
 * @createDate 2025-03-18 23:39:20
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {
    @Resource
    private UserService userService;

    @Override
    public void validateArticle(ArticleAddRequest articleAddRequest) {
        String title = articleAddRequest.getTitle();
        String author = articleAddRequest.getAuthor();
        String content = articleAddRequest.getContent();
        String coverImage = articleAddRequest.getCoverImage();
        List<String> contentImages = articleAddRequest.getContentImages();
        ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR, "标题不能为空");
        ThrowUtils.throwIf(title.length() > 40, ErrorCode.PARAMS_ERROR, "标题过长");
        ThrowUtils.throwIf(StringUtils.isBlank(author), ErrorCode.PARAMS_ERROR, "作者不能为空");
        ThrowUtils.throwIf(StringUtils.isBlank(coverImage), ErrorCode.PARAMS_ERROR, "封面不能为空");
        ThrowUtils.throwIf(contentImages.size() > 10, ErrorCode.PARAMS_ERROR, "上传内容图片，一次不能超过十张");
        ThrowUtils.throwIf(content.length() > 10000, ErrorCode.PARAMS_ERROR, "内容过长");
    }

    @Override
    public QueryWrapper<Article> getQueryWrapper(ArticleQueryRequest articleQueryRequest) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        if (articleQueryRequest == null) {
            return queryWrapper;
        }
        //  从对象中取值
        Long id = articleQueryRequest.getId();
        String title = articleQueryRequest.getTitle();
        String author = articleQueryRequest.getAuthor();
        List<String> tagList = articleQueryRequest.getTags();
        Integer priority = articleQueryRequest.getPriority();
        int current = articleQueryRequest.getCurrent();
        int pageSize = articleQueryRequest.getPageSize();
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();
        String searchText = articleQueryRequest.getSearchText();
        Long userId = articleQueryRequest.getUserId();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or()
                    .like("author", searchText).or()
            );
        }
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(author), "description", author);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(priority), "priority", priority);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public ArticleVO getArticleVO(Article article, HttpServletRequest request) {
        // 对象转封装类
        ArticleVO articleVO = ArticleVO.objToVo(article);
        Long userId = article.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        articleVO.setUserVO(userVO);
        return articleVO;
    }

    @Override
    public Page<ArticleVO> getArticleVOPage(Page<Article> articlePage, HttpServletRequest request) {
        List<Article> articleList = articlePage.getRecords();
        Page<ArticleVO> articleVOPage = new Page<>(articlePage.getCurrent(), articlePage.getSize(), articlePage.getTotal());
        if (CollUtil.isEmpty(articleList)) {
            return articleVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ArticleVO> articleVOList = articleList.stream().map(ArticleVO::objToVo).collect(Collectors.toList());

        // 1. 关联查询用户信息
        Set<Long> userIdSet = articleList.stream().map(Article::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2，填充信息
        articleVOList.forEach(articleVO -> {
            Long userId = articleVO.getUserId();
            User user = new User();
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            articleVO.setUserVO(userService.getUserVO(user));
        });

        articleVOPage.setRecords(articleVOList);
        return articleVOPage;
    }
}






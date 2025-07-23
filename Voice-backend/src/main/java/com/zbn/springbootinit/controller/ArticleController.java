package com.zbn.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbn.springbootinit.common.*;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.exception.ThrowUtils;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.manager.TTSManager;
import com.zbn.springbootinit.model.dto.article.ArticleAddRequest;
import com.zbn.springbootinit.model.dto.article.ArticleQueryRequest;
import com.zbn.springbootinit.model.dto.article.ArticleUpdateRequest;
import com.zbn.springbootinit.model.dto.article.ChapterAddRequest;
import com.zbn.springbootinit.model.entity.Article;
import com.zbn.springbootinit.model.entity.Bookshelf;
import com.zbn.springbootinit.model.entity.Chapter;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.ArticleVO;
import com.zbn.springbootinit.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
@Slf4j
public class ArticleController {
    @Resource
    private ArticleService articleService;
    @Resource
    private UserService userService;
    @Resource
    TTSManager ttsManager;
    @Resource
    FileManager fileManager;
    @Resource
    ImageToTextService imageToTextService;
    @Resource
    BookshelfService bookshelfService;
    @Resource
    ChapterService chapterService;

    @PostMapping("/add")
    public BaseResponse<Long> addArticle(@RequestBody ArticleAddRequest articleAddRequest,
                                         HttpServletRequest request) {
        if (articleAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        articleService.validateArticle(articleAddRequest);
        Article article = new Article();
        if (articleAddRequest.getContentImages().isEmpty()) {
            ThrowUtils.throwIf(articleAddRequest.getContent() == null, ErrorCode.PARAMS_ERROR, "内容不能为空");
            String rawContent = articleAddRequest.getContent();
            List<String> contentList = List.of(rawContent.split("\\r?\\n|\\r"));
            String content = JSONUtil.toJsonStr(contentList);
            article.setContent(content);
        } else {
            String rawContent = imageToTextService.getImageText(articleAddRequest.getContentImages());
            List<String> contentList = List.of(rawContent.split("\\r?\\n|\\r"));
            String content = JSONUtil.toJsonStr(contentList);
            article.setContent(content);
        }
        Long userId = userService.getLoginUser(request).getId();
        if (!articleAddRequest.getSummary().isEmpty()) {
            String rawSummary = articleAddRequest.getSummary();
            List<String> summaryList = List.of(rawSummary.split("\\r?\\n|\\r"));
            String summary = JSONUtil.toJsonStr(summaryList);
            article.setSummary(summary);
        } else {
            String content = article.getContent();
            List<String> contentList = JSONUtil.toList(content, String.class);
            String summary = JSONUtil.toJsonStr(contentList.subList(0, 5));
            article.setSummary(summary);
        }
        article.setTitle(articleAddRequest.getTitle());
        article.setAuthor(articleAddRequest.getAuthor());
        article.setCoverImage(articleAddRequest.getCoverImage());
        article.setUserId(userId);
        article.setPriority(1);
        boolean result = articleService.save(article);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(article.getId());
    }

    @PostMapping("/update")
    public BaseResponse<Long> updateArticle(@RequestBody ArticleUpdateRequest articleUpdateRequest,
                                            HttpServletRequest request) {
        if (articleUpdateRequest == null || articleUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数错误");
        }
        Article article = articleService.getById(articleUpdateRequest.getId());
        String oldContent = article.getContent();
        List<String> oldContentList = JSONUtil.toList(oldContent, String.class);
        if (articleUpdateRequest.getContentImages().isEmpty()) {
            ThrowUtils.throwIf(articleUpdateRequest.getContent() == null, ErrorCode.PARAMS_ERROR, "内容不能为空");
            String rawContent = articleUpdateRequest.getContent();
            List<String> contentList = List.of(rawContent.split("\\r?\\n|\\r"));
            oldContentList.addAll(contentList);
            String content = JSONUtil.toJsonStr(oldContentList);
            article.setContent(content);
        } else {
            String rawContent = imageToTextService.getImageText(articleUpdateRequest.getContentImages());
            List<String> contentList = List.of(rawContent.split("\\r?\\n|\\r"));
            oldContentList.addAll(contentList);
            String content = JSONUtil.toJsonStr(oldContentList);
            article.setContent(content);
        }
        Long userId = userService.getLoginUser(request).getId();
        if (!articleUpdateRequest.getSummary().isEmpty()) {
            String rawSummary = articleUpdateRequest.getSummary();
            List<String> summaryList = List.of(rawSummary.split("\\r?\\n|\\r"));
            String summary = JSONUtil.toJsonStr(summaryList);
            article.setSummary(summary);
        } else {
            String content = article.getContent();
            List<String> contentList = JSONUtil.toList(content, String.class);
            String summary = JSONUtil.toJsonStr(contentList.subList(0, 5));
            article.setSummary(summary);
        }
        article.setTitle(articleUpdateRequest.getTitle());
        article.setAuthor(articleUpdateRequest.getAuthor());
        article.setCoverImage(articleUpdateRequest.getCoverImage());
        article.setUserId(userId);
        article.setPriority(1);
        boolean result = articleService.updateById(article);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(article.getId());
    }

    /**
     * 上传封面
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/coverImg")
    public BaseResponse<String> uploadCoverImg(@RequestPart("file") MultipartFile multipartFile,
                                               HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        String filePath = fileManager.uploadFile(multipartFile, String.format("public/article/%s/coverImg", userId));
        ThrowUtils.throwIf(filePath == null, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(filePath);
    }

    /**
     * 上传内容图片
     *
     * @param multipartFile
     * @param request
     * @return
     */
    @PostMapping("/upload/contentImg")
    public BaseResponse<String> uploadContentImg(@RequestPart("file") MultipartFile multipartFile,
                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        String filePath = fileManager.uploadFile(multipartFile, String.format("public/article/%s/contentImag", userId));
        ThrowUtils.throwIf(filePath == null, ErrorCode.SYSTEM_ERROR);
        return ResultUtils.success(filePath);
    }

    @PostMapping("/get/top")
    public BaseResponse<List<Article>> getTopArticle() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("priority");
        queryWrapper.last("limit 6");
        return ResultUtils.success(articleService.list(queryWrapper));
    }

    @PostMapping("/get/all")
    public BaseResponse<List<Article>> getAllArticle() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("priority");
        return ResultUtils.success(articleService.list(queryWrapper));
    }

    @GetMapping("/get/vo")
    public BaseResponse<ArticleVO> getArticleVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Article article = articleService.getById(id);
        ThrowUtils.throwIf(article == null, ErrorCode.NOT_FOUND_ERROR);
        ArticleVO articleVO = ArticleVO.objToVo(article);
//        User user = userService.getLoginUser(request);
//        articleVO.setUserVO(userService.getUserVO(user));
        return ResultUtils.success(articleVO);
    }

    @PostMapping("/read")
    public BaseResponse<String> readArticle(@RequestBody ReadArticleRequest readArticleRequest) {
        if (readArticleRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String filePath = ttsManager.postTTs(readArticleRequest.getText(), "zh", readArticleRequest.getType());
        if (filePath == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(filePath);
    }

    /**
     * 分页获取文章列表（封装类）
     *
     * @param articleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ArticleVO>> listArticleVOByPage(@RequestBody ArticleQueryRequest articleQueryRequest,
                                                             HttpServletRequest request) {
        long current = articleQueryRequest.getCurrent();
        long size = articleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Article> articlePage = articleService.page(new Page<>(current, size),
                articleService.getQueryWrapper(articleQueryRequest));
        // 获取封装类
        return ResultUtils.success(articleService.getArticleVOPage(articlePage, request));
    }

    /**
     * 分页获取文章列表（封装类）
     *
     * @param articleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Article>> listArticleByPage(@RequestBody ArticleQueryRequest articleQueryRequest,
                                                         HttpServletRequest request) {
        long current = articleQueryRequest.getCurrent();
        long size = articleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Article> articlePage = articleService.page(new Page<>(current, size), articleService.getQueryWrapper(articleQueryRequest));
        // 获取封装类
        return ResultUtils.success(articlePage);
    }

    @PostMapping("/add/bookshelf")
    public BaseResponse<Long> addBookshelf(@RequestBody Map<String, Long> requestBody, HttpServletRequest request) {
        Long articleId = requestBody.get("articleId");
        if (articleId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        Article article = articleService.getById(articleId);
        if (article == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        Bookshelf bookshelf = new Bookshelf();
        bookshelf.setArticleId(articleId);
        bookshelf.setUserId(loginUser.getId());
        boolean result = bookshelfService.save(bookshelf);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(articleId);
    }

    @GetMapping("/get/bookshelf")
    public BaseResponse<List<ArticleVO>> getBookshelf(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Bookshelf> bookshelfList = bookshelfService.list(new QueryWrapper<Bookshelf>().eq("userId", loginUser.getId()));
        // 获取ArticleID 集合（无重复元素）
        List<Long> articleIdList = bookshelfList.stream().map(Bookshelf::getArticleId).collect(Collectors.toList());
        List<Article> articleList = articleService.list(new QueryWrapper<Article>().in("id", articleIdList));
        return ResultUtils.success(articleList.stream().map(ArticleVO::objToVo).collect(Collectors.toList()));
    }

    @PostMapping("/list/my/books")
    public BaseResponse<Page<ArticleVO>> listMyBooks(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        QueryWrapper<Bookshelf> queryBookshelf = new QueryWrapper<>();
        queryBookshelf.eq("userId", userService.getLoginUser(request).getId());
        List<Bookshelf> bookshelfList = bookshelfService.list(queryBookshelf);
        // 如果没有收藏记录，则返回空结果
        if (bookshelfList.isEmpty()) {
            return ResultUtils.success(new Page<>(current, size));
        }
        bookshelfList.forEach(bookshelf -> {
            Article article = articleService.getById(bookshelf.getArticleId());
            ThrowUtils.throwIf(article == null, ErrorCode.NOT_FOUND_ERROR);
        });
        List<Long> articleIds = bookshelfList.stream().map(Bookshelf::getArticleId).toList();
        queryWrapper.in("id", articleIds);
        // 查询数据库
        Page<Article> articlePage = articleService.page(new Page<>(current, size), queryWrapper);
        // 获取封装类
        return ResultUtils.success(articleService.getArticleVOPage(articlePage, request));
    }

    @PostMapping("/list/create/books")
    public BaseResponse<Page<ArticleVO>> listCreateBooks(@RequestBody PageRequest pageRequest, HttpServletRequest request) {
        long current = pageRequest.getCurrent();
        long size = pageRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userService.getLoginUser(request).getId());
        // 查询数据库
        Page<Article> articlePage = articleService.page(new Page<>(current, size), queryWrapper);
        // 获取封装类
        return ResultUtils.success(articleService.getArticleVOPage(articlePage, request));
    }

    @PostMapping("/is/myBook")
    public BaseResponse<Boolean> isMyBook(Long articleId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        queryWrapper.eq("id", articleId);
        Article article = articleService.getOne(queryWrapper);
        return ResultUtils.success(article != null);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteArticle(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Article oldArticle = articleService.getById(id);
        ThrowUtils.throwIf(oldArticle == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldArticle.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = articleService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 同步删除书架中的文章
        QueryWrapper<Bookshelf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("articleId", id);
        if (bookshelfService.getOne(queryWrapper) != null) {
            boolean result1 = bookshelfService.remove(queryWrapper);
            ThrowUtils.throwIf(!result1, ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/list/chapter")
    public BaseResponse<List<Chapter>> listChapter(Long articleId) {
        if (articleId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("articleId", articleId);
        queryWrapper.orderByAsc("chapterOrder");
        List<Chapter> chapterList = chapterService.list(queryWrapper);
        return ResultUtils.success(chapterList);
    }

    @GetMapping("/get/chapter")
    public BaseResponse<Chapter> getChapter(Long chapterId) {
        if (chapterId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chapter chapter = chapterService.getById(chapterId);
        return ResultUtils.success(chapter);
    }

    @PostMapping("/add/chapter")
    public BaseResponse<Long> addChapter(@RequestBody ChapterAddRequest chapterAddRequest, HttpServletRequest request) {
        if (chapterAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chapter chapter = new Chapter();
        BeanUtils.copyProperties(chapterAddRequest, chapter);
//        String rawContent = chapterAddRequest.getContent();
//        List<String> contentList = List.of(rawContent.split("\\r?\\n|\\r"));
//        String content = JSONUtil.toJsonStr(contentList);
//        chapter.setContent(content);
        boolean result = chapterService.save(chapter);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(chapter.getId());
    }
}

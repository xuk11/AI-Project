package com.zbn.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaFormat;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaInfoRequest;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaInfoResponse;
import com.qcloud.cos.model.ciModel.mediaInfo.MediaInfoStreamObject;
import com.zbn.springbootinit.annotation.AuthCheck;
import com.zbn.springbootinit.common.BaseResponse;
import com.zbn.springbootinit.common.DeleteRequest;
import com.zbn.springbootinit.common.ErrorCode;
import com.zbn.springbootinit.common.ResultUtils;
import com.zbn.springbootinit.config.CosClientConfig;
import com.zbn.springbootinit.constant.UserConstant;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.exception.ThrowUtils;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.model.dto.audioCommunity.AudioCommunityQueryRequest;
import com.zbn.springbootinit.model.dto.audioCommunity.UpdateAudio;
import com.zbn.springbootinit.model.dto.file.UploadFileRequest;
import com.zbn.springbootinit.model.entity.AudioCommunity;
import com.zbn.springbootinit.model.entity.LikeAudio;
import com.zbn.springbootinit.model.entity.PlayAudio;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.AudioCommunityVO;
import com.zbn.springbootinit.service.AudioCommunityService;
import com.zbn.springbootinit.service.LikeAudioService;
import com.zbn.springbootinit.service.PlayAudioService;
import com.zbn.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 音频社区模块
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 * @date 2025/04/01
 */
@RestController
@RequestMapping("/audioCommunity")
@Slf4j
public class AudioCommunityController {

    @Resource
    private AudioCommunityService audioCommunityService;

    @Resource
    private UserService userService;
    @Resource
    private FileManager fileManager;
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private LikeAudioService likeAudioService;
    @Resource
    private PlayAudioService playAudioService;

    // region 增删改查

    /**
     * 判断是否点赞
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/isLiked")
    public BaseResponse<Boolean> isLiked(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }
        Long userId = loginUser.getId();
        LambdaQueryWrapper<LikeAudio> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LikeAudio::getUserId, userId)
                .eq(LikeAudio::getAudioId, id);
        LikeAudio likeAudio = likeAudioService.getOne(queryWrapper);
        return ResultUtils.success(likeAudio != null);
    }

    /**
     * 增加点赞次数
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/like")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Integer> likeAudio(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }

        // 验证音频存在性（不查询全字段）
        boolean exists = audioCommunityService.lambdaQuery()
                .eq(AudioCommunity::getId, id)
                .exists();
        ThrowUtils.throwIf(!exists, ErrorCode.NOT_FOUND_ERROR, "音频文件不存在");

        Long userId = loginUser.getId();
        LambdaQueryWrapper<LikeAudio> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LikeAudio::getUserId, userId)
                .eq(LikeAudio::getAudioId, id);

        LikeAudio oldLikeAudio = likeAudioService.getOne(queryWrapper);
        boolean isLike = oldLikeAudio != null;

        // 使用原子操作更新点赞数
        LambdaUpdateWrapper<AudioCommunity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AudioCommunity::getId, id)
                .setSql(isLike ? "likeCount = likeCount - 1" : "likeCount = likeCount + 1");

        if (isLike) {
            likeAudioService.remove(queryWrapper);
        } else {
            LikeAudio likeAudio = new LikeAudio();
            likeAudio.setUserId(userId);
            likeAudio.setAudioId(id);
            likeAudioService.save(likeAudio);
        }

        boolean result = audioCommunityService.update(updateWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(audioCommunityService.getById(id).getLikeCount());
    }


    /**
     * 增加播放次数
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/play")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Integer> playAudio(Long id, HttpServletRequest request) {
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
        }

        AudioCommunity audioCommunity = audioCommunityService.getById(id);
        ThrowUtils.throwIf(audioCommunity == null, ErrorCode.NOT_FOUND_ERROR, "音频文件不存在");

        Long userId = loginUser.getId();
        boolean exists = playAudioService.lambdaQuery()
                .eq(PlayAudio::getUserId, userId)
                .eq(PlayAudio::getAudioId, id)
                .exists();
        if (exists) {
            return ResultUtils.success(audioCommunity.getPlayCount());
        }

        try {
            // 插入播放记录
            PlayAudio playAudio = new PlayAudio();
            playAudio.setUserId(userId);
            playAudio.setAudioId(id);
            playAudioService.save(playAudio);

            // 原子更新播放次数
            boolean updateSuccess = audioCommunityService.lambdaUpdate()
                    .setSql("playCount = playCount + 1")
                    .eq(AudioCommunity::getId, id)
                    .update();
            ThrowUtils.throwIf(!updateSuccess, ErrorCode.OPERATION_ERROR);

        } catch (DuplicateKeyException e) {
            // 处理并发插入导致的唯一键冲突
            log.debug("重复播放记录已存在: userId={}, audioId={}", userId, id);
        }

        return ResultUtils.success(audioCommunityService.getById(id).getPlayCount());
    }


    /**
     * 获取自定义音频
     *
     * @param request
     * @return
     */
    @PostMapping("/get/customAudio")
    public BaseResponse<List<AudioCommunityVO>> getCustomAudio(HttpServletRequest request) {
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            return ResultUtils.success(null);
        }
        Long userId = loginUser.getId();
        QueryWrapper<AudioCommunity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 4);
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("id");
        List<AudioCommunity> AudioCommunityList = audioCommunityService.list(queryWrapper);
        List<AudioCommunityVO> AudioCommunityVOList = AudioCommunityList.stream().map(AudioCommunityVO::objToVo).toList();
        return ResultUtils.success(AudioCommunityVOList);
    }

    /**
     * 上传音频文件
     *
     * @param multipartFile
     * @param uploadFileRequest
     * @return
     */
    @PostMapping("/upload")
    public BaseResponse<String> uploadAudio(@RequestPart("file") MultipartFile multipartFile,
                                            UploadFileRequest uploadFileRequest,
                                            HttpServletRequest request) {
        Long userId = userService.getLoginUser(request).getId();
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        String dateStr = year + "-" + month + "-" + day;
        String biz = uploadFileRequest.getBiz();
        String uploadPathPrefix = String.format("public/%s/%s", biz, dateStr);
        String filePath = fileManager.uploadVideo(multipartFile, uploadPathPrefix);
        // 创建媒体信息请求对象
        MediaInfoRequest mediaInfoRequest = new MediaInfoRequest();
        mediaInfoRequest.setBucketName(cosClientConfig.getBucket());
        mediaInfoRequest.getInput().setObject(filePath);
        MediaInfoResponse response = cosClientConfig.cosClient().generateMediainfo(mediaInfoRequest);
        MediaInfoStreamObject mediaInfo = response.getMediaInfo();
        MediaFormat format = mediaInfo.getFormat();
        filePath = cosClientConfig.getHost() + "/" + filePath.substring(1).replace("/", "%2F");
        String fileSize = format.getSize();
        String duration = format.getDuration();
        AudioCommunity audioCommunity = new AudioCommunity();
        audioCommunity.setTitle(uploadFileRequest.getTitle());
        audioCommunity.setFileName(multipartFile.getOriginalFilename());
        audioCommunity.setFilePath(filePath);
        audioCommunity.setFileSize(Long.valueOf(fileSize));
        audioCommunity.setFileType("audio/wav");
        audioCommunity.setType(4);
        audioCommunity.setUserId(userId);
        audioCommunity.setDuration(duration);
        audioCommunity.setPlayCount(0);
        audioCommunity.setLikeCount(0);
        audioCommunity.setIsShare(uploadFileRequest.getIsShare());
        audioCommunityService.save(audioCommunity);
        return ResultUtils.success(audioCommunity.getFilePath());
    }

    /**
     * 创建音频文件
     *
     * @param multipartFile
     * @param uploadPathPrefix
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAudioCommunity(@RequestPart("file") MultipartFile multipartFile,
                                                String uploadPathPrefix, int type, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        String filePath = fileManager.uploadVideo(multipartFile, uploadPathPrefix);
        // 创建媒体信息请求对象
        MediaInfoRequest mediaInfoRequest = new MediaInfoRequest();
        mediaInfoRequest.setBucketName(cosClientConfig.getBucket());
        mediaInfoRequest.getInput().setObject(filePath);
        MediaInfoResponse response = cosClientConfig.cosClient().generateMediainfo(mediaInfoRequest);
        MediaInfoStreamObject mediaInfo = response.getMediaInfo();
        MediaFormat format = mediaInfo.getFormat();
        filePath = cosClientConfig.getHost() + "/" + filePath.substring(1).replace("/", "%2F");
        String fileSize = format.getSize(); // 文件大小
        String duration = format.getDuration(); // 视频时长
        AudioCommunity audioCommunity = new AudioCommunity();
        audioCommunity.setFileName(multipartFile.getOriginalFilename());
        audioCommunity.setFilePath(filePath);
        audioCommunity.setPicture("https://01-1325205761.cos.ap-nanjing.myqcloud.com/voice%2Fimages.jpg");
        audioCommunity.setFileSize(Long.valueOf(fileSize));
        audioCommunity.setFileType("audio/wav");
        List<String> tags = new ArrayList<>();
        audioCommunity.setTags(JSONUtil.toJsonStr(tags));
        audioCommunity.setType(type);
        audioCommunity.setDuration(duration);
        audioCommunity.setUserId(userId);
        audioCommunity.setPlayCount(0);
        audioCommunity.setLikeCount(0);
        audioCommunityService.save(audioCommunity);
        return ResultUtils.success(audioCommunity.getId());
    }

    /**
     * 删除音频文件
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAudioCommunity(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        AudioCommunity oldAudioCommunity = audioCommunityService.getById(id);
        ThrowUtils.throwIf(oldAudioCommunity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldAudioCommunity.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = audioCommunityService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }


    /**
     * 根据 id 获取音频文件（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<AudioCommunityVO> getAudioCommunityVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        AudioCommunity audioCommunity = audioCommunityService.getById(id);
        ThrowUtils.throwIf(audioCommunity == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类·
        return ResultUtils.success(audioCommunityService.getAudioCommunityVO(audioCommunity, request));
    }

    /**
     * 分页获取音频文件列表（仅管理员可用）
     *
     * @param audioCommunityQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AudioCommunity>> listAudioCommunityByPage(@RequestBody AudioCommunityQueryRequest audioCommunityQueryRequest) {
        long current = audioCommunityQueryRequest.getCurrent();
        long size = audioCommunityQueryRequest.getPageSize();
        // 查询数据库
        Page<AudioCommunity> audioCommunityPage = audioCommunityService.page(new Page<>(current, size),
                audioCommunityService.getQueryWrapper(audioCommunityQueryRequest));
        return ResultUtils.success(audioCommunityPage);
    }

    /**
     * 分页获取音频文件列表（封装类）
     *
     * @param audioCommunityQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AudioCommunityVO>> listAudioCommunityVOByPage(@RequestBody AudioCommunityQueryRequest audioCommunityQueryRequest,
                                                                           HttpServletRequest request) {
        long current = audioCommunityQueryRequest.getCurrent();
        long size = audioCommunityQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 100, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<AudioCommunity> audioCommunityPage = audioCommunityService.page(new Page<>(current, size),
                audioCommunityService.getQueryWrapper(audioCommunityQueryRequest));
        // 获取封装类
        return ResultUtils.success(audioCommunityService.getAudioCommunityVOPage(audioCommunityPage, request));
    }

    /**
     * 分页获取当前登录用户创建的音频文件列表
     *
     * @param audioCommunityQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AudioCommunityVO>> listMyAudioCommunityVOByPage(@RequestBody AudioCommunityQueryRequest audioCommunityQueryRequest,
                                                                             HttpServletRequest request) {
        ThrowUtils.throwIf(audioCommunityQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        audioCommunityQueryRequest.setUserId(loginUser.getId());
        long current = audioCommunityQueryRequest.getCurrent();
        long size = audioCommunityQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<AudioCommunity> audioCommunityPage = audioCommunityService.page(new Page<>(current, size),
                audioCommunityService.getQueryWrapper(audioCommunityQueryRequest));
        // 获取封装类
        return ResultUtils.success(audioCommunityService.getAudioCommunityVOPage(audioCommunityPage, request));
    }

    @PostMapping
    public BaseResponse<Boolean> updateAudio(@RequestBody UpdateAudio updateAudio, HttpServletRequest request) {
        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        Long id = updateAudio.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        AudioCommunity oldAudioCommunity = audioCommunityService.getById(id);
        ThrowUtils.throwIf(oldAudioCommunity == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可修改
        if (!oldAudioCommunity.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        AudioCommunity audioCommunity = new AudioCommunity();
        audioCommunity.setId(id);
        audioCommunity.setTitle(updateAudio.getTitle());
        audioCommunity.setIsShare(updateAudio.getIsShare());
        boolean result = audioCommunityService.updateById(audioCommunity);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}

package com.zbn.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.zbn.springbootinit.model.dto.audiofile.AudioFileQueryRequest;
import com.zbn.springbootinit.model.dto.file.UploadFileRequest;
import com.zbn.springbootinit.model.entity.AudioFile;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.AudioFileVO;
import com.zbn.springbootinit.service.AudioFileService;
import com.zbn.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 音频文件模块
 *
 * @author <a href="https://github.com/qwerzbn">zbn</a>
 * @date 2025/02/09
 */
@RestController
@RequestMapping("/audioFile")
@Slf4j
public class AudioFileController {

    @Resource
    private AudioFileService AudioFileService;
    @Resource
    private UserService userService;
    @Resource
    private FileManager fileManager;
    @Resource
    private CosClientConfig cosClientConfig;

    // region 增删改查
    @PostMapping("/get/customAudio")
    public BaseResponse<List<AudioFileVO>> getCustomAudio(HttpServletRequest request) {
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser == null) {
            return ResultUtils.success(null);
        }
        Long userId = loginUser.getId();
        QueryWrapper<AudioFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", 4);
        queryWrapper.eq("userId", userId);
        queryWrapper.orderByDesc("id");
        List<AudioFile> AudioFileList = AudioFileService.list(queryWrapper);
        List<AudioFileVO> AudioFileVOList = AudioFileList.stream().map(AudioFileVO::objToVo).toList();
        return ResultUtils.success(AudioFileVOList);
    }

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
        String fileSize = format.getSize(); // 文件大小
        String duration = format.getDuration(); // 视频时长
        AudioFile AudioFile = new AudioFile();
        AudioFile.setTitle(uploadFileRequest.getTitle());
        AudioFile.setFileName(multipartFile.getOriginalFilename());
        AudioFile.setFilePath(filePath);
        AudioFile.setPicture("https://01-1325205761.cos.ap-nanjing.myqcloud.com/voice%2Fimages.jpg");
        AudioFile.setFileSize(Long.valueOf(fileSize));
        AudioFile.setFileType("audio/wav");
        AudioFile.setType(4);
        AudioFile.setUserId(userId);
        AudioFile.setDuration(duration);
        AudioFileService.save(AudioFile);
        return ResultUtils.success(AudioFile.getFilePath());
    }

    /**
     * 创建音频文件
     *
     * @param multipartFile
     * @param uploadPathPrefix
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addAudioFile(@RequestPart("file") MultipartFile multipartFile,
                                           String uploadPathPrefix, int type) {
//        LocalDate date = LocalDate.now();
//        int year = date.getYear();
//        int month = date.getMonthValue();
//        int day = date.getDayOfMonth();
//        String dateStr = year + "-" + month + "-" + day;
//        String biz = uploadFileRequest.getBiz();
//        String uploadPathPrefix = String.format("public/%s/%s", biz, dateStr);
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
        AudioFile AudioFile = new AudioFile();
        AudioFile.setFileName(multipartFile.getOriginalFilename());
        AudioFile.setFilePath(filePath);
        AudioFile.setPicture("https://01-1325205761.cos.ap-nanjing.myqcloud.com/voice%2Fimages.jpg");
        AudioFile.setFileSize(Long.valueOf(fileSize));
        AudioFile.setFileType("audio/wav");
        List<String> tags = new ArrayList<>();
        AudioFile.setTags(JSONUtil.toJsonStr(tags));
        AudioFile.setType(type);
        AudioFile.setDuration(duration);
        AudioFileService.save(AudioFile);
        return ResultUtils.success(AudioFile.getId());
    }

    /**
     * 删除音频文件
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAudioFile(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        AudioFile oldAudioFile = AudioFileService.getById(id);
        ThrowUtils.throwIf(oldAudioFile == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldAudioFile.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = AudioFileService.removeById(id);
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
    public BaseResponse<AudioFileVO> getAudioFileVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        AudioFile AudioFile = AudioFileService.getById(id);
        ThrowUtils.throwIf(AudioFile == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(AudioFileService.getAudioFileVO(AudioFile, request));
    }

    /**
     * 分页获取音频文件列表（仅管理员可用）
     *
     * @param AudioFileQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AudioFile>> listAudioFileByPage(@RequestBody AudioFileQueryRequest AudioFileQueryRequest) {
        long current = AudioFileQueryRequest.getCurrent();
        long size = AudioFileQueryRequest.getPageSize();
        // 查询数据库
        Page<AudioFile> AudioFilePage = AudioFileService.page(new Page<>(current, size),
                AudioFileService.getQueryWrapper(AudioFileQueryRequest));
        return ResultUtils.success(AudioFilePage);
    }

    /**
     * 分页获取音频文件列表（封装类）
     *
     * @param AudioFileQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<AudioFileVO>> listAudioFileVOByPage(@RequestBody AudioFileQueryRequest AudioFileQueryRequest,
                                                                 HttpServletRequest request) {
        long current = AudioFileQueryRequest.getCurrent();
        long size = AudioFileQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 100, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<AudioFile> AudioFilePage = AudioFileService.page(new Page<>(current, size),
                AudioFileService.getQueryWrapper(AudioFileQueryRequest));
        // 获取封装类
        return ResultUtils.success(AudioFileService.getAudioFileVOPage(AudioFilePage, request));
    }

    /**
     * 分页获取当前登录用户创建的音频文件列表
     *
     * @param AudioFileQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<AudioFileVO>> listMyAudioFileVOByPage(@RequestBody AudioFileQueryRequest AudioFileQueryRequest,
                                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(AudioFileQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        AudioFileQueryRequest.setUserId(loginUser.getId());
        long current = AudioFileQueryRequest.getCurrent();
        long size = AudioFileQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<AudioFile> AudioFilePage = AudioFileService.page(new Page<>(current, size),
                AudioFileService.getQueryWrapper(AudioFileQueryRequest));
        // 获取封装类
        return ResultUtils.success(AudioFileService.getAudioFileVOPage(AudioFilePage, request));
    }
    // endregion
}

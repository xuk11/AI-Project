package com.zbn.springbootinit.controller;

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
import com.zbn.springbootinit.model.dto.Auditoryppt.AuditorypptQueryRequest;
import com.zbn.springbootinit.model.dto.file.UploadFileRequest;
import com.zbn.springbootinit.model.dto.video.UpdateVideo;
import com.zbn.springbootinit.model.entity.Auditoryppt;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.service.AuditorypptService;
import com.zbn.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/auditoryppt")
@Slf4j
public class AuditorypptController {
    @Resource
    AuditorypptService auditorypptService;
    @Resource
    UserService userService;
    @Resource
    FileManager fileManager;
    @Resource
    private CosClientConfig cosClientConfig;

    @PostMapping("/update")
    public BaseResponse<Boolean> updateAuditoryppt(@RequestBody UpdateVideo updateVideo, HttpServletRequest request) {
        if (updateVideo == null || updateVideo.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Auditoryppt auditoryppt = auditorypptService.getById(updateVideo.getId());
        ThrowUtils.throwIf(auditoryppt == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(auditoryppt.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        auditoryppt.setName(updateVideo.getTitle());
        boolean result = auditorypptService.updateById(auditoryppt);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);

    }

    @PostMapping("/list")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<List<Auditoryppt>> videoList(@RequestBody AuditorypptQueryRequest auditorypptQueryRequest) {
        QueryWrapper<Auditoryppt> queryWrapper = new QueryWrapper<>();
        List<Auditoryppt> AuditorypptList = auditorypptService.list(queryWrapper);
        return ResultUtils.success(AuditorypptList);
    }

    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Auditoryppt>> listAuditorypptByPage(@RequestBody AuditorypptQueryRequest auditorypptQueryRequest) {
        QueryWrapper<Auditoryppt> queryWrapper = new QueryWrapper<>();
        long current = auditorypptQueryRequest.getCurrent();
        long size = auditorypptQueryRequest.getPageSize();
        // 查询数据库
        Page<Auditoryppt> AuditorypptPage = auditorypptService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(AuditorypptPage);
    }

    @PostMapping("/list/my")
    public BaseResponse<List<Auditoryppt>> getMyAuditorypptList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        Long userId = loginUser.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Auditoryppt> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Auditoryppt> AuditorypptList = auditorypptService.list(queryWrapper);
        return ResultUtils.success(AuditorypptList);
    }

    @PostMapping("/upload")
    public BaseResponse<String> upload(@RequestParam("file") MultipartFile file, UploadFileRequest uploadFileRequest,
                                       HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        Long userId = loginUser.getId();
        LocalDate date = LocalDate.now();
        int year = date.getYear();
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        String dateStr = year + "-" + month + "-" + day;
        String biz = uploadFileRequest.getBiz();
        String uploadPathPrefix = String.format("public/%s/%s", biz, dateStr);
        String uploadPath = fileManager.uploadVideo(file, uploadPathPrefix);
        // 创建媒体信息请求对象
        MediaInfoRequest mediaInfoRequest = new MediaInfoRequest();
        mediaInfoRequest.setBucketName(cosClientConfig.getBucket());
        mediaInfoRequest.getInput().setObject(uploadPath);
        MediaInfoResponse response = cosClientConfig.cosClient().generateMediainfo(mediaInfoRequest);
        // 从MediaInfoResponse中获取所需信息
        MediaInfoStreamObject mediaInfo = response.getMediaInfo();
        MediaFormat format = mediaInfo.getFormat();

        String fileSize = format.getSize(); // 文件大小
        String duration = format.getDuration(); // 视频时长
        String filePath = cosClientConfig.getHost() + "/" + uploadPath.substring(1).replace("/", "%2F");
        Auditoryppt auditoryppt = new Auditoryppt();
        auditoryppt.setName("Test");
        auditoryppt.setFilePath(filePath);
        auditoryppt.setFileSize(Long.valueOf(fileSize));
        auditoryppt.setFileType("video/mp4");
        auditoryppt.setUserId(userId);
        auditoryppt.setDuration(duration);
        auditorypptService.save(auditoryppt);
        return ResultUtils.success(filePath);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteAuditoryppt(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Auditoryppt oldAuditoryppt = auditorypptService.getById(id);
        ThrowUtils.throwIf(oldAuditoryppt == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldAuditoryppt.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = auditorypptService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }
}

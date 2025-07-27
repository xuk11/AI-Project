package com.zbn.springbootinit.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zbn.springbootinit.common.*;
import com.zbn.springbootinit.config.CosClientConfig;
import com.zbn.springbootinit.exception.BusinessException;
import com.zbn.springbootinit.exception.ThrowUtils;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.manager.MyWebSocketHandler;
import com.zbn.springbootinit.manager.TTSManager;
import com.zbn.springbootinit.model.dto.asr.ASRequest;
import com.zbn.springbootinit.model.dto.asr.ASResponse;
import com.zbn.springbootinit.model.dto.file.UploadFileRequest;
import com.zbn.springbootinit.model.dto.video.UpdateVideo;
import com.zbn.springbootinit.model.dto.video.VideoQueryRequest;
import com.zbn.springbootinit.model.dto.video.VideoRequest;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.entity.Video;
import com.zbn.springbootinit.service.PptCacheService;
import com.zbn.springbootinit.service.UserService;
import com.zbn.springbootinit.service.VideoService;
import com.zbn.springbootinit.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.zbn.springbootinit.controller.PPTController.convert;

@RestController
@RequestMapping("/video")
@Slf4j
public class VideoController {
    @Resource
    VideoService videoService;
    @Resource
    UserService userService;
    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private FileManager fileManager;
    @Resource
    TTSManager ttsManager;
    @Resource
    MyWebSocketHandler myWebSocketHandler;
    @Resource
    FileUtils fileUtils;

    @Resource
    private RedissonClient redissonClient;

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
        String filePath = videoService.uploadVideo(file, uploadPathPrefix, userId);
        return ResultUtils.success(filePath);
    }

    @PostMapping("/getUrl")
    public BaseResponse<String> getVideoUrl(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String uploadPathPrefix = "tempFile";
        String uploadPath = fileManager.uploadFile(file, uploadPathPrefix);
        return ResultUtils.success(uploadPath);
    }

    @PostMapping("/reset")
    public BaseResponse<String> VideoReset(@RequestBody VideoRequest videoRequest, HttpServletRequest request) throws IOException, NoSuchAlgorithmException, InterruptedException {
        Long userId = userService.getLoginUser(request).getId();
        try {
            PptCacheService cacheService = new PptCacheService(redissonClient);
            ProgressMessage message = new ProgressMessage(0, "", "progress", "videoReset");
            videoService.updateProgress(message, 0, 10, "正在发起请求....");
            String url = "http://i-2.gpushare.com:37210/get/videoAudio";
            String getVideoAudioStr = JSONUtil.toJsonStr(videoRequest);
            String Response = HttpUtil.createPost(url).body(getVideoAudioStr).execute().body();
            message.setProgress(20);
            message.setMessage("音频分离完成....");
            myWebSocketHandler.broadcast(message);
            // 上传音频到云端，获取视频音频分离后的文件路径
            VideoResetMessage videoResetMessage = JSONUtil.toBean(Response, VideoResetMessage.class);
            message.setProgress(40);
            message.setMessage("正在调用ASR模型.....");
            myWebSocketHandler.broadcast(message);
            String FILE_HASH = videoResetMessage.getHash();
            ThrowUtils.throwIf(StringUtils.isEmpty(FILE_HASH), ErrorCode.OPERATION_ERROR, "文件Hash生成失败");
            String cacheText = cacheService.getCachedText(FILE_HASH);
            if (cacheText == null || cacheText.isEmpty()) {
                ASRequest asRequest = new ASRequest();
                asRequest.setInput(videoResetMessage.getFilePath());
                asRequest.setModel("达摩 ASR (中文)");
                asRequest.setModelSize("large");
                asRequest.setLang("zh");
                asRequest.setPrecision("float32");
                String ASRequestStr = JSONUtil.toJsonStr(asRequest);
                url = "http://i-2.gpushare.com:37210/asr";
                Response = HttpUtil.createPost(url).body(ASRequestStr).execute().body();
                ASResponse asResponse = JSONUtil.toBean(Response, ASResponse.class);
                String Text = asResponse.getText();
                // 找到最后一个'|'的位置
                int index = Text.lastIndexOf('|');
                // 如果找到了'|'，则提取该位置之后的所有字符
                if (index != -1) {
                    Text = Text.substring(index + 1);
                }
                cacheService.cacheText(FILE_HASH, Text);
                cacheText = Text;
            }
            message.setProgress(60);
            message.setMessage("文本解析完成.....");
            myWebSocketHandler.broadcast(message);
            String filePath = ttsManager.postTTs(cacheText, "zh", videoRequest.getType());
            MultipartFile file = convert(filePath);
            String uploadPath = fileManager.uploadFile(file, "tempFile");
            message.setProgress(70);
            message.setMessage("正在处理视频和语音的合成.....");
            myWebSocketHandler.broadcast(message);
            videoResetMessage.setFilePath(uploadPath);
            String newVideoPath = videoService.processVideoSynthesis(message, videoResetMessage);
            message.setProgress(90);
            message.setMessage("合成成功，正在自动生成字幕.....");
            myWebSocketHandler.broadcast(message);
            GenerateCaptionsRequest generateCaptionsRequest = new GenerateCaptionsRequest(videoResetMessage.getTaskId(), newVideoPath, videoRequest.getLanguage());
            String captionsRequestStr = JSONUtil.toJsonStr(generateCaptionsRequest);
            url = "http://i-2.gpushare.com:37210/generate/captions";
            Response = HttpUtil.createPost(url).body(captionsRequestStr).execute().body();
            GenerateCaptionsResult generateCaptionsResult = JSONUtil.toBean(Response, GenerateCaptionsResult.class);
            message.setProgress(99);
            message.setMessage("字幕生成完成，正在上传至个人中心.....");
            myWebSocketHandler.broadcast(message);
            Video video = new Video();
            video.setName(DateUtil.formatDate(new Date()) + "置换视频");
            video.setFileSize(generateCaptionsResult.getFileSize());
            video.setFileType("video/mp4");
            video.setUserId(userId);
            video.setDuration(generateCaptionsResult.getDuration());
            video.setFilePath(generateCaptionsResult.getFilePath());
            boolean result = videoService.save(video);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "视频保存失败");
            message.setProgress(100);
            message.setMessage("已完成");
            message.setStatus("success");
            myWebSocketHandler.broadcast(message);
            return ResultUtils.success(video.getFilePath());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }

    @PostMapping("/list/page")
    public BaseResponse<Page<Video>> listVideoByPage(@RequestBody VideoQueryRequest videoQueryRequest) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        long current = videoQueryRequest.getCurrent();
        long size = videoQueryRequest.getPageSize();
        // 查询数据库
        Page<Video> VideoPage = videoService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(VideoPage);
    }

    @PostMapping("/list")
    public BaseResponse<List<Video>> videoList(@RequestBody VideoQueryRequest videoQueryRequest) {
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        List<Video> Videolist = videoService.list(queryWrapper);
        return ResultUtils.success(Videolist);
    }

    @PostMapping("/list/my")
    public BaseResponse<List<Video>> getMyVideoList(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "未登录");
        }
        Long userId = loginUser.getId();
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<Video> Videolist = videoService.list(queryWrapper);
        return ResultUtils.success(Videolist);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteVideo(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Video oldVideo = videoService.getById(id);
        ThrowUtils.throwIf(oldVideo == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldVideo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = videoService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateVideo(@RequestBody UpdateVideo updateVideo, HttpServletRequest request) {
        if (updateVideo == null || updateVideo.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Video video = videoService.getById(updateVideo.getId());
        ThrowUtils.throwIf(video == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        if (!userService.isAdmin(loginUser) && !loginUser.getId().equals(video.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        video.setName(updateVideo.getTitle());
        boolean result = videoService.updateById(video);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);

    }
}

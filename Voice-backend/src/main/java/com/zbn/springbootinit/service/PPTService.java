package com.zbn.springbootinit.service;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.zbn.springbootinit.common.GenerateCaptionsResult;
import com.zbn.springbootinit.common.ProgressMessage;
import com.zbn.springbootinit.manager.CosManager;
import com.zbn.springbootinit.manager.FileManager;
import com.zbn.springbootinit.manager.MyWebSocketHandler;
import com.zbn.springbootinit.manager.TTSManager;
import com.zbn.springbootinit.model.dto.ppt.CreateVideoRequest;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.zbn.springbootinit.controller.PPTController.convert;

@Service
@RequiredArgsConstructor
public class PPTService {
    @Resource
    private ImageToTextService imageToTextService;
    @Resource
    private TTSManager ttsManager;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    MyWebSocketHandler myWebSocketHandler;
    @Resource
    private CosManager cosManager;
    @Resource
    private FileManager fileManager;

    public GenerateCaptionsResult convertPPT(ArrayList<String> imgUrl, int type, String fileHash) throws IOException {
        ProgressMessage progressMessage = new ProgressMessage(0, "", "progress", "ppt");
        CreateVideoRequest createVideoRequest = new CreateVideoRequest();
        String resultText = "";
        PptCacheService cacheService = new PptCacheService(redissonClient);
        // 检查缓存
        String cachedText = cacheService.getCachedText(fileHash);
        if (cachedText != null) {
            // 使用缓存
            resultText = cachedText;
        } else {
            progressMessage.setProgress(10);
            progressMessage.setMessage("正在解析PPT，生成解析文本....");
            myWebSocketHandler.broadcast(progressMessage);
            resultText = imageToTextService.getText(imgUrl);
            // 缓存解析结果
            cacheService.cacheText(fileHash, resultText);
        }
        List<String> textList = Arrays.stream(resultText.split("---END PAGE---"))
                .filter(s -> !s.isEmpty())
                .toList();
        progressMessage.setProgress(40);
        progressMessage.setMessage("解析完成，正在生成音频....");
        myWebSocketHandler.broadcast(progressMessage);
        ArrayList<String> audioUrlList = new ArrayList<>();
        for (int i = 0; i < imgUrl.size(); i++) {
            String text = textList.get(i);
            if (text != null && !text.isEmpty()) {
                String filePath = ttsManager.postTTs(text, "zh", type);
                MultipartFile file = convert(filePath);
                String uploadPath = fileManager.uploadFile(file, "tempFile");
                audioUrlList.add(uploadPath);
                progressMessage.setMessage(String.format("正在生成音频 ----- %s/%s", i + 1, imgUrl.size()));
                progressMessage.setProgress(progressMessage.getProgress() + 45 / imgUrl.size());
                myWebSocketHandler.broadcast(progressMessage);
            }
        }
        createVideoRequest.setAudioList(audioUrlList);
        createVideoRequest.setImageList(imgUrl);
        return createVideo(createVideoRequest);
    }

    public GenerateCaptionsResult createVideo(CreateVideoRequest createVideoRequest) throws IOException {
        ProgressMessage progressMessage = new ProgressMessage(0, "", "progress", "ppt");
        String url = "http://localhost:8000/generate/video";
        String createVideoStr = JSONUtil.toJsonStr(createVideoRequest);
        progressMessage.setMessage("解析完成，正在合成有声PPT....");
        progressMessage.setProgress(85);
        myWebSocketHandler.broadcast(progressMessage);
        String Response = HttpUtil.createPost(url).body(createVideoStr).execute().body();
        GenerateCaptionsResult generateCaptionsResult = JSONUtil.toBean(Response, GenerateCaptionsResult.class);
        return generateCaptionsResult;
    }
}
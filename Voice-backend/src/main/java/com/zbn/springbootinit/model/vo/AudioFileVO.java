package com.zbn.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.zbn.springbootinit.model.entity.AudioFile;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class AudioFileVO {
    /**
     * 音频文件唯一ID
     */
    private Long id;

    /**
     * 音频文件原始文件名
     */
    private String fileName;

    /**
     * 音频文件存储路径
     */
    private String filePath;

    /**
     * 音频文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频文件MIME类型（如audio/mp3）
     */
    private String fileType;

    /**
     * 音频时长（秒）
     */
    private String duration;

    /**
     * 上传文件的用户ID（可选）
     */
    private Long userId;

    /**
     * 音频标题（可选）
     */
    private String title;

    /**
     * 音频描述（可选）
     */
    private String description;
    /**
     * 图片路径
     */
    private String picture;
    /**
     * 标签
     */
    private List<String> tags;

    private Integer type;
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
    private Integer isDelete;

    /**
     * 用户信息
     */
    private UserVO userVO;

    public static AudioFileVO objToVo(AudioFile AudioFile) {
        if (AudioFile == null) return null;
        AudioFileVO AudioFileVO = new AudioFileVO();
        BeanUtils.copyProperties(AudioFile, AudioFileVO);
        if (AudioFile.getTags() != null) {
            AudioFileVO.setTags(JSONUtil.toList(AudioFile.getTags(), String.class));
        }
        return AudioFileVO;
    }

    public AudioFile voToObj(AudioFileVO AudioFileVO) {
        if (AudioFileVO == null) return null;
        AudioFile AudioFile = new AudioFile();
        BeanUtils.copyProperties(AudioFileVO, AudioFile);
        if (AudioFileVO.getTags() != null) {
            AudioFile.setTags(JSONUtil.toJsonStr(AudioFileVO.getTags()));
        }
        return AudioFile;
    }
}

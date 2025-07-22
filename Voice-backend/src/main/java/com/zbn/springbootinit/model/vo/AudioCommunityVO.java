package com.zbn.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.zbn.springbootinit.model.entity.AudioCommunity;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class AudioCommunityVO {
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
     * 播放次数
     */
    private Integer playCount;

    /**
     * 点赞次数
     */
    private Integer likeCount;
    /**
     * 是否分享
     */
    private Integer isShare;
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

    public static AudioCommunityVO objToVo(AudioCommunity audioCommunity) {
        if (audioCommunity == null) return null;
        AudioCommunityVO audioCommunityVO = new AudioCommunityVO();
        BeanUtils.copyProperties(audioCommunity, audioCommunityVO);
        if (audioCommunity.getTags() != null) {
            audioCommunityVO.setTags(JSONUtil.toList(audioCommunity.getTags(), String.class));
        }
        return audioCommunityVO;
    }

    public AudioCommunity voToObj(AudioCommunityVO audioCommunityVO) {
        if (audioCommunityVO == null) return null;
        AudioCommunity audioCommunity = new AudioCommunity();
        BeanUtils.copyProperties(audioCommunityVO, audioCommunity);
        if (audioCommunityVO.getTags() != null) {
            audioCommunity.setTags(JSONUtil.toJsonStr(audioCommunityVO.getTags()));
        }
        return audioCommunity;
    }
}

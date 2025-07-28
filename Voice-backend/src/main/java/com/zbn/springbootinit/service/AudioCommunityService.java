package com.zbn.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zbn.springbootinit.model.dto.audioCommunity.AudioCommunityQueryRequest;
import com.zbn.springbootinit.model.entity.AudioCommunity;
import com.zbn.springbootinit.model.vo.AudioCommunityVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qwer
 * @description 针对表【audio_community(音频文件存储表)】的数据库操作Service
 * @createDate 2025-04-08 00:10:51
 */
public interface AudioCommunityService extends IService<AudioCommunity> {

    /**
     * 获取查询条件
     *
     * @param audioCommunityQueryRequest
     * @return
     */
    QueryWrapper<AudioCommunity> getQueryWrapper(AudioCommunityQueryRequest audioCommunityQueryRequest);

    /**
     * 获取音频文件封装
     *
     * @param audioCommunity
     * @param request
     * @return
     */
    AudioCommunityVO getAudioCommunityVO(AudioCommunity audioCommunity, HttpServletRequest request);

    /**
     * 分页获取音频文件封装
     *
     * @param audioCommunityPage
     * @param request
     * @return
     */
    Page<AudioCommunityVO> getAudioCommunityVOPage(Page<AudioCommunity> audioCommunityPage, HttpServletRequest request);
}

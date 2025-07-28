package com.zbn.springbootinit.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.constant.CommonConstant;
import com.zbn.springbootinit.mapper.AudioCommunityMapper;
import com.zbn.springbootinit.model.dto.audioCommunity.AudioCommunityQueryRequest;
import com.zbn.springbootinit.model.entity.AudioCommunity;
import com.zbn.springbootinit.model.entity.User;
import com.zbn.springbootinit.model.vo.AudioCommunityVO;
import com.zbn.springbootinit.model.vo.UserVO;
import com.zbn.springbootinit.service.AudioCommunityService;
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
 * @description 针对表【audio_community(音频文件存储表)】的数据库操作Service实现
 * @createDate 2025-04-08 00:10:51
 */
@Service
public class AudioCommunityServiceImpl extends ServiceImpl<AudioCommunityMapper, AudioCommunity>
        implements AudioCommunityService {
    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<AudioCommunity> getQueryWrapper(AudioCommunityQueryRequest audioCommunityQueryRequest) {
        QueryWrapper<AudioCommunity> queryWrapper = new QueryWrapper<>();
        if (audioCommunityQueryRequest == null) {
            return queryWrapper;
        }
        //  从对象中取值
        Long id = audioCommunityQueryRequest.getId();
        Long userId = audioCommunityQueryRequest.getUserId();
        String title = audioCommunityQueryRequest.getTitle();
        String description = audioCommunityQueryRequest.getDescription();
        String sortField = audioCommunityQueryRequest.getSortField();
        String sortOrder = audioCommunityQueryRequest.getSortOrder();
        String searchText = audioCommunityQueryRequest.getSearchText();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("description", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public AudioCommunityVO getAudioCommunityVO(AudioCommunity audioCommunity, HttpServletRequest request) {
        // 对象转封装类
        AudioCommunityVO audioCommunityVO = AudioCommunityVO.objToVo(audioCommunity);
        // 关联查询用户信息
        Long userId = audioCommunity.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        audioCommunityVO.setUserVO(userVO);
        return audioCommunityVO;
    }

    @Override
    public Page<AudioCommunityVO> getAudioCommunityVOPage(Page<AudioCommunity> audioCommunityPage, HttpServletRequest request) {
        List<AudioCommunity> AudioCommunityList = audioCommunityPage.getRecords();
        Page<AudioCommunityVO> AudioCommunityVOPage = new Page<>(audioCommunityPage.getCurrent(), audioCommunityPage.getSize(), audioCommunityPage.getTotal());
        if (CollUtil.isEmpty(AudioCommunityList)) {
            return AudioCommunityVOPage;
        }
        // 对象列表 => 封装对象列表
        List<AudioCommunityVO> AudioCommunityVOList = AudioCommunityList.stream().map(AudioCommunityVO::objToVo).toList();

        // 1. 关联查询用户信息
        Set<Long> userIdSet = AudioCommunityList.stream().map(AudioCommunity::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2，填充信息
        AudioCommunityVOList.forEach(AudioFileVO -> {
            Long userId = AudioFileVO.getUserId();
            User user = new User();
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            AudioFileVO.setUserVO(userService.getUserVO(user));
        });

        AudioCommunityVOPage.setRecords(AudioCommunityVOList);
        return AudioCommunityVOPage;
    }
}





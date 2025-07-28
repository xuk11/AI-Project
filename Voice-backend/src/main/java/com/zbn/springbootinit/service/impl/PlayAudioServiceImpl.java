package com.zbn.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.mapper.PlayAudioMapper;
import com.zbn.springbootinit.model.entity.PlayAudio;
import com.zbn.springbootinit.service.PlayAudioService;
import org.springframework.stereotype.Service;

/**
 * @author qwer
 * @description 针对表【play_audio(音频播放次数表)】的数据库操作Service实现
 * @createDate 2025-04-08 21:33:15
 */
@Service
public class PlayAudioServiceImpl extends ServiceImpl<PlayAudioMapper, PlayAudio>
        implements PlayAudioService {

}





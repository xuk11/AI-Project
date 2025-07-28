package com.zbn.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.mapper.SpaceMapper;
import com.zbn.springbootinit.model.entity.Space;
import com.zbn.springbootinit.service.SpaceService;
import org.springframework.stereotype.Service;

/**
 * @author qwer
 * @description 针对表【space(有声PPT)】的数据库操作Service实现
 * @createDate 2025-02-28 19:15:53
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

}

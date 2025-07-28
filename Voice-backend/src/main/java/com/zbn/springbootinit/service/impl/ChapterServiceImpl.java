package com.zbn.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.mapper.ChapterMapper;
import com.zbn.springbootinit.model.entity.Chapter;
import com.zbn.springbootinit.service.ChapterService;
import org.springframework.stereotype.Service;

/**
 * @author qwer
 * @description 针对表【Chapter(书本章节表)】的数据库操作Service实现
 * @createDate 2025-03-24 10:17:57
 */
@Service
public class ChapterServiceImpl extends ServiceImpl<ChapterMapper, Chapter>
        implements ChapterService {

}





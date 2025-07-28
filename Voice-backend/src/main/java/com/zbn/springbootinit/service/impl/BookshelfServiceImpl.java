package com.zbn.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbn.springbootinit.mapper.BookshelfMapper;
import com.zbn.springbootinit.model.entity.Bookshelf;
import com.zbn.springbootinit.service.BookshelfService;
import org.springframework.stereotype.Service;

/**
 * @author qwer
 * @description 针对表【Bookshelf(书架，保存用户收藏的书)】的数据库操作Service实现
 * @createDate 2025-03-23 11:49:13
 */
@Service
public class BookshelfServiceImpl extends ServiceImpl<BookshelfMapper, Bookshelf>
        implements BookshelfService {

}





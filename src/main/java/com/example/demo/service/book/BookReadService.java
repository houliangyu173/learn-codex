package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookReadInfo;
import com.example.demo.mapper.book.BookMapper;
import org.springframework.stereotype.Service;

/**
 * 阅读服务，负责组装阅读页面所需的数据。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookReadService {

    private final BookMapper bookMapper;

    public BookReadService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public BookReadInfo getReadInfo(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return null;
        }
        return new BookReadInfo(book.getId(), book.getTitle(), book.getFileType(), book.getReadUrl());
    }
}

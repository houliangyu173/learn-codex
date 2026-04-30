package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookQueryCondition;
import com.example.demo.domain.book.PageResult;
import com.example.demo.mapper.book.BookMapper;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 书籍查询服务，负责列表和详情查询编排。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookQueryService {

    private final BookMapper bookMapper;

    public BookQueryService(BookMapper bookMapper) {
        this.bookMapper = bookMapper;
    }

    public PageResult<Book> queryPage(BookQueryCondition condition) {
        List<Book> records = bookMapper.selectByCondition(condition);
        long total = bookMapper.countByCondition(condition);
        return new PageResult<Book>(records, total);
    }

    public Book getDetail(Long id) {
        return bookMapper.selectById(id);
    }
}

package com.example.demo.mapper.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookQueryCondition;
import java.util.List;

/**
 * 书籍数据访问接口，为后续切换 MyBatis-Plus 预留稳定边界。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public interface BookMapper {

    List<Book> selectByCondition(BookQueryCondition condition);

    long countByCondition(BookQueryCondition condition);

    Book selectById(Long id);

    Book updateStatus(Long id, Integer status);

    int batchSave(List<Book> books);

    boolean existsBySourceAndSourceBookId(String source, String sourceBookId);
}

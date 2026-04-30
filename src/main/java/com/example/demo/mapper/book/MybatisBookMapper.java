package com.example.demo.mapper.book;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookQueryCondition;
import com.example.demo.entity.BookEntity;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Repository;

/**
 * 基于 MyBatis-Plus 的书籍数据访问实现。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Repository
public class MybatisBookMapper implements BookMapper {

    private final BookEntityMapper bookEntityMapper;

    public MybatisBookMapper(BookEntityMapper bookEntityMapper) {
        this.bookEntityMapper = bookEntityMapper;
    }

    @Override
    public List<Book> selectByCondition(BookQueryCondition condition) {
        long offset = Math.max((long) (condition.getPageNum() - 1) * condition.getPageSize(), 0L);
        List<BookEntity> entities = bookEntityMapper.selectPageWithCategory(
                condition.getKeyword(), condition.getCategoryId(), offset, condition.getPageSize());
        return convertList(entities);
    }

    @Override
    public long countByCondition(BookQueryCondition condition) {
        return bookEntityMapper.countPage(condition.getKeyword(), condition.getCategoryId());
    }

    @Override
    public Book selectById(Long id) {
        return convert(bookEntityMapper.selectDetailById(id));
    }

    @Override
    public Book updateStatus(Long id, Integer status) {
        BookEntity entity = new BookEntity();
        entity.setId(id);
        entity.setStatus(status);
        bookEntityMapper.updateById(entity);
        return selectById(id);
    }

    @Override
    public int batchSave(List<Book> books) {
        int successCount = 0;
        for (Book book : books) {
            if (existsBySourceAndSourceBookId(book.getSource(), book.getSourceBookId())) {
                continue;
            }
            BookEntity entity = convert(book);
            entity.setId(null);
            bookEntityMapper.insert(entity);
            successCount++;
        }
        return successCount;
    }

    @Override
    public boolean existsBySourceAndSourceBookId(String source, String sourceBookId) {
        Long count = bookEntityMapper.selectCount(new LambdaQueryWrapper<BookEntity>()
                .eq(BookEntity::getSource, source)
                .eq(BookEntity::getSourceBookId, sourceBookId));
        return count != null && count > 0;
    }

    private List<Book> convertList(List<BookEntity> entities) {
        List<Book> books = new ArrayList<Book>();
        for (BookEntity entity : entities) {
            books.add(convert(entity));
        }
        return books;
    }

    private Book convert(BookEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getDescription(),
                entity.getCoverUrl(),
                entity.getSource(),
                entity.getSourceBookId(),
                entity.getLanguage(),
                entity.getCategoryId(),
                entity.getCategoryName(),
                entity.getFileType(),
                entity.getReadUrl(),
                entity.getDownloadUrl(),
                entity.getStatus()
        );
    }

    private BookEntity convert(Book book) {
        BookEntity entity = new BookEntity();
        entity.setId(book.getId());
        entity.setTitle(book.getTitle());
        entity.setAuthor(book.getAuthor());
        entity.setDescription(book.getDescription());
        entity.setCoverUrl(book.getCoverUrl());
        entity.setSource(book.getSource());
        entity.setSourceBookId(book.getSourceBookId());
        entity.setLanguage(book.getLanguage());
        entity.setCategoryId(book.getCategoryId());
        entity.setFileType(book.getFileType());
        entity.setReadUrl(book.getReadUrl());
        entity.setDownloadUrl(book.getDownloadUrl());
        entity.setStatus(book.getStatus());
        entity.setSyncStatus("SUCCESS");
        return entity;
    }
}

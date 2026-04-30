package com.example.demo.service.book;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.domain.book.BookCategory;
import com.example.demo.entity.BookCategoryEntity;
import com.example.demo.mapper.book.BookCategoryEntityMapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 书籍分类服务。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookCategoryService {

    private final BookCategoryEntityMapper bookCategoryEntityMapper;

    public BookCategoryService(BookCategoryEntityMapper bookCategoryEntityMapper) {
        this.bookCategoryEntityMapper = bookCategoryEntityMapper;
    }

    public List<BookCategory> listEnabledCategories() {
        List<BookCategoryEntity> entities = bookCategoryEntityMapper.selectList(
                new LambdaQueryWrapper<BookCategoryEntity>()
                        .eq(BookCategoryEntity::getStatus, 1)
                        .orderByAsc(BookCategoryEntity::getSort, BookCategoryEntity::getId)
        );
        List<BookCategory> categories = new ArrayList<BookCategory>();
        for (BookCategoryEntity entity : entities) {
            BookCategory category = new BookCategory();
            category.setId(entity.getId());
            category.setName(entity.getName());
            category.setParentId(entity.getParentId());
            category.setSort(entity.getSort());
            category.setStatus(entity.getStatus());
            categories.add(category);
        }
        return categories;
    }
}

package com.example.demo.service.book;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.domain.book.BookCategory;
import com.example.demo.entity.BookCategoryEntity;
import com.example.demo.mapper.book.BookCategoryEntityMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        List<BookCategoryEntity> entities = listEnabledCategoryEntities();
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

    public Long resolveCategoryId(String text) {
        String normalizedText = text == null ? "" : text.toLowerCase();
        Map<String, List<String>> keywordMap = new LinkedHashMap<String, List<String>>();
        keywordMap.put("儿童", Arrays.asList("children", "child", "juvenile", "fairy", "nursery", "boys", "girls"));
        keywordMap.put("技术", Arrays.asList("computer", "technology", "engineering", "programming", "technical"));
        keywordMap.put("科学", Arrays.asList("science", "physics", "astronomy", "chemistry", "biology", "universe"));
        keywordMap.put("历史", Arrays.asList("history", "war", "historical", "ancient", "civilization"));
        keywordMap.put("小说", Arrays.asList("novel", "fiction", "romance", "story", "tale", "prejudice"));
        for (Map.Entry<String, List<String>> entry : keywordMap.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (normalizedText.contains(keyword)) {
                    return findCategoryIdByName(entry.getKey());
                }
            }
        }
        return findCategoryIdByName("小说");
    }

    private List<BookCategoryEntity> listEnabledCategoryEntities() {
        List<BookCategoryEntity> entities = bookCategoryEntityMapper.selectList(
                new LambdaQueryWrapper<BookCategoryEntity>()
                        .eq(BookCategoryEntity::getStatus, 1)
                        .orderByAsc(BookCategoryEntity::getSort, BookCategoryEntity::getId)
        );
        return entities;
    }

    private Long findCategoryIdByName(String name) {
        List<BookCategoryEntity> entities = listEnabledCategoryEntities();
        for (BookCategoryEntity entity : entities) {
            if (name.equals(entity.getName())) {
                return entity.getId();
            }
        }
        return entities.isEmpty() ? null : entities.get(0).getId();
    }
}

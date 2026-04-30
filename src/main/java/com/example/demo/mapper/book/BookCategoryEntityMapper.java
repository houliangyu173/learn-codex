package com.example.demo.mapper.book;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.BookCategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍分类实体 Mapper。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Mapper
public interface BookCategoryEntityMapper extends BaseMapper<BookCategoryEntity> {
}

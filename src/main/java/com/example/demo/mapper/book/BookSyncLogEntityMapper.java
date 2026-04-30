package com.example.demo.mapper.book;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.BookSyncLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 书籍同步日志实体 Mapper。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Mapper
public interface BookSyncLogEntityMapper extends BaseMapper<BookSyncLogEntity> {
}

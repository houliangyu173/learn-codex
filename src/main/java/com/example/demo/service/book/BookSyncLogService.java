package com.example.demo.service.book;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.domain.book.BookSyncLog;
import com.example.demo.domain.book.PageResult;
import com.example.demo.entity.BookSyncLogEntity;
import com.example.demo.mapper.book.BookSyncLogEntityMapper;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 书籍同步日志服务。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookSyncLogService {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final BookSyncLogEntityMapper bookSyncLogEntityMapper;

    public BookSyncLogService(BookSyncLogEntityMapper bookSyncLogEntityMapper) {
        this.bookSyncLogEntityMapper = bookSyncLogEntityMapper;
    }

    public PageResult<BookSyncLog> queryPage(int pageNum, int pageSize) {
        int offset = Math.max((pageNum - 1) * pageSize, 0);
        List<BookSyncLogEntity> entities = bookSyncLogEntityMapper.selectList(
                new LambdaQueryWrapper<BookSyncLogEntity>()
                        .orderByDesc(BookSyncLogEntity::getId)
                        .last("limit " + offset + "," + pageSize)
        );
        Long total = bookSyncLogEntityMapper.selectCount(new LambdaQueryWrapper<BookSyncLogEntity>());
        List<BookSyncLog> records = new ArrayList<BookSyncLog>();
        for (BookSyncLogEntity entity : entities) {
            BookSyncLog log = new BookSyncLog();
            log.setId(entity.getId());
            log.setSource(entity.getSource());
            log.setTriggerType(entity.getTriggerType());
            log.setSuccessCount(entity.getSuccessCount());
            log.setFailCount(entity.getFailCount());
            log.setStatus(entity.getStatus());
            log.setMessage(entity.getMessage());
            log.setStartTime(entity.getStartTime() == null ? null : entity.getStartTime().format(DATETIME_FORMATTER));
            log.setEndTime(entity.getEndTime() == null ? null : entity.getEndTime().format(DATETIME_FORMATTER));
            records.add(log);
        }
        return new PageResult<BookSyncLog>(records, total == null ? 0L : total);
    }
}

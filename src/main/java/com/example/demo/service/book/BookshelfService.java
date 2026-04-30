package com.example.demo.service.book;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.domain.book.BookshelfAddRequest;
import com.example.demo.domain.book.BookshelfItem;
import com.example.demo.domain.book.BookshelfProgressUpdateRequest;
import com.example.demo.domain.book.PageResult;
import com.example.demo.entity.UserBookshelfEntity;
import com.example.demo.mapper.book.UserBookshelfEntityMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 用户书架服务。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookshelfService {

    private static final Long DEFAULT_USER_ID = 1L;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final UserBookshelfEntityMapper userBookshelfEntityMapper;

    public BookshelfService(UserBookshelfEntityMapper userBookshelfEntityMapper) {
        this.userBookshelfEntityMapper = userBookshelfEntityMapper;
    }

    public BookshelfItem add(BookshelfAddRequest request) {
        Long userId = normalizeUserId(request.getUserId());
        UserBookshelfEntity entity = userBookshelfEntityMapper.selectOne(
                new LambdaQueryWrapper<UserBookshelfEntity>()
                        .eq(UserBookshelfEntity::getUserId, userId)
                        .eq(UserBookshelfEntity::getBookId, request.getBookId())
                        .last("limit 1")
        );
        if (entity == null) {
            entity = new UserBookshelfEntity();
            entity.setUserId(userId);
            entity.setBookId(request.getBookId());
            entity.setReadProgress(0);
            entity.setLastReadTime(LocalDateTime.now());
            userBookshelfEntityMapper.insert(entity);
        }
        return getItem(userId, request.getBookId());
    }

    public BookshelfItem updateProgress(BookshelfProgressUpdateRequest request) {
        Long userId = normalizeUserId(request.getUserId());
        UserBookshelfEntity entity = userBookshelfEntityMapper.selectOne(
                new LambdaQueryWrapper<UserBookshelfEntity>()
                        .eq(UserBookshelfEntity::getUserId, userId)
                        .eq(UserBookshelfEntity::getBookId, request.getBookId())
                        .last("limit 1")
        );
        if (entity == null) {
            BookshelfAddRequest addRequest = new BookshelfAddRequest();
            addRequest.setUserId(userId);
            addRequest.setBookId(request.getBookId());
            add(addRequest);
            entity = userBookshelfEntityMapper.selectOne(
                    new LambdaQueryWrapper<UserBookshelfEntity>()
                            .eq(UserBookshelfEntity::getUserId, userId)
                            .eq(UserBookshelfEntity::getBookId, request.getBookId())
                            .last("limit 1")
            );
        }
        entity.setReadProgress(normalizeProgress(request.getReadProgress()));
        entity.setLastReadTime(LocalDateTime.now());
        userBookshelfEntityMapper.updateById(entity);
        return getItem(userId, request.getBookId());
    }

    public PageResult<BookshelfItem> queryPage(Long userId, int pageNum, int pageSize) {
        Long normalizedUserId = normalizeUserId(userId);
        long offset = Math.max((long) (pageNum - 1) * pageSize, 0L);
        List<UserBookshelfEntity> entities = userBookshelfEntityMapper.selectPageByUserId(normalizedUserId, offset, pageSize);
        Long total = userBookshelfEntityMapper.selectCount(
                new LambdaQueryWrapper<UserBookshelfEntity>().eq(UserBookshelfEntity::getUserId, normalizedUserId)
        );
        List<BookshelfItem> records = new ArrayList<BookshelfItem>();
        for (UserBookshelfEntity entity : entities) {
            records.add(convert(entity));
        }
        return new PageResult<BookshelfItem>(records, total == null ? 0L : total);
    }

    private BookshelfItem getItem(Long userId, Long bookId) {
        List<UserBookshelfEntity> entities = userBookshelfEntityMapper.selectPageByUserId(userId, 0, 100);
        for (UserBookshelfEntity entity : entities) {
            if (bookId.equals(entity.getBookId())) {
                return convert(entity);
            }
        }
        return null;
    }

    private BookshelfItem convert(UserBookshelfEntity entity) {
        BookshelfItem item = new BookshelfItem();
        item.setId(entity.getId());
        item.setUserId(entity.getUserId());
        item.setBookId(entity.getBookId());
        item.setTitle(entity.getTitle());
        item.setAuthor(entity.getAuthor());
        item.setCoverUrl(entity.getCoverUrl());
        item.setCategoryName(entity.getCategoryName());
        item.setReadProgress(entity.getReadProgress());
        item.setLastReadTime(entity.getLastReadTime() == null ? null
                : entity.getLastReadTime().format(DATETIME_FORMATTER));
        return item;
    }

    private Long normalizeUserId(Long userId) {
        return userId == null ? DEFAULT_USER_ID : userId;
    }

    private int normalizeProgress(Integer readProgress) {
        if (readProgress == null) {
            return 0;
        }
        if (readProgress < 0) {
            return 0;
        }
        return Math.min(readProgress, 100);
    }
}

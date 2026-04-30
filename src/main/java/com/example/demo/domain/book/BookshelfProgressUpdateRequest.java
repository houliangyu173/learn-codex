package com.example.demo.domain.book;

/**
 * 书架阅读进度更新请求对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookshelfProgressUpdateRequest {

    private Long userId;
    private Long bookId;
    private Integer readProgress;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getReadProgress() {
        return readProgress;
    }

    public void setReadProgress(Integer readProgress) {
        this.readProgress = readProgress;
    }
}

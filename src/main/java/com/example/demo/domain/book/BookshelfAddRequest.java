package com.example.demo.domain.book;

/**
 * 加入书架请求对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookshelfAddRequest {

    private Long userId;
    private Long bookId;

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
}

package com.example.demo.domain.book;

/**
 * 书籍同步结果对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookSyncResult {

    private int successCount;
    private String message;

    public BookSyncResult() {
    }

    public BookSyncResult(int successCount, String message) {
        this.successCount = successCount;
        this.message = message;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

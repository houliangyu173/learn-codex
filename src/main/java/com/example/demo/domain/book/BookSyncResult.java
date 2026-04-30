package com.example.demo.domain.book;

/**
 * 书籍同步结果对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookSyncResult {

    private String source;
    private String supplementSource;
    private int supplementCount;
    private int successCount;
    private int failCount;
    private String message;

    public BookSyncResult() {
    }

    public BookSyncResult(String source, int successCount, int failCount, String message) {
        this.source = source;
        this.successCount = successCount;
        this.failCount = failCount;
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSupplementSource() {
        return supplementSource;
    }

    public void setSupplementSource(String supplementSource) {
        this.supplementSource = supplementSource;
    }

    public int getSupplementCount() {
        return supplementCount;
    }

    public void setSupplementCount(int supplementCount) {
        this.supplementCount = supplementCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

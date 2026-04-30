package com.example.demo.domain.book;

/**
 * 同步日志查询条件对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookSyncLogQuery {

    private int pageNum = 1;
    private int pageSize = 10;
    private String source;
    private String status;
    private String triggerType;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
}

package com.example.demo.domain.book;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果对象，供列表接口统一返回。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class PageResult<T> {

    private List<T> records = Collections.emptyList();
    private long total;

    public PageResult() {
    }

    public PageResult(List<T> records, long total) {
        this.records = records;
        this.total = total;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}

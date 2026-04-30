package com.example.demo.domain.book;

/**
 * 书籍查询条件对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookQueryCondition {

    private String keyword;
    private Long categoryId;
    private int pageNum = 1;
    private int pageSize = 10;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

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
}

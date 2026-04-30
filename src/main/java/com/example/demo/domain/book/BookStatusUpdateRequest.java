package com.example.demo.domain.book;

/**
 * 书籍状态更新请求对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookStatusUpdateRequest {

    private Long id;
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

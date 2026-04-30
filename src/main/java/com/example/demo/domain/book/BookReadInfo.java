package com.example.demo.domain.book;

/**
 * 阅读页所需信息对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookReadInfo {

    private Long id;
    private String title;
    private String fileType;
    private String readUrl;

    public BookReadInfo() {
    }

    public BookReadInfo(Long id, String title, String fileType, String readUrl) {
        this.id = id;
        this.title = title;
        this.fileType = fileType;
        this.readUrl = readUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getReadUrl() {
        return readUrl;
    }

    public void setReadUrl(String readUrl) {
        this.readUrl = readUrl;
    }
}

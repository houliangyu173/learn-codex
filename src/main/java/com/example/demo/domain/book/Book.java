package com.example.demo.domain.book;

/**
 * 书籍领域对象，承载前后端联调所需的核心字段。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class Book {

    private Long id;
    private String title;
    private String author;
    private String description;
    private String coverUrl;
    private String source;
    private String sourceBookId;
    private String language;
    private Long categoryId;
    private String categoryName;
    private String fileType;
    private String readUrl;
    private String downloadUrl;
    private Integer status;

    public Book() {
    }

    public Book(Long id, String title, String author, String description, String coverUrl, String source,
                String sourceBookId, String language, Long categoryId, String categoryName, String fileType,
                String readUrl, String downloadUrl, Integer status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.coverUrl = coverUrl;
        this.source = source;
        this.sourceBookId = sourceBookId;
        this.language = language;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.fileType = fileType;
        this.readUrl = readUrl;
        this.downloadUrl = downloadUrl;
        this.status = status;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceBookId() {
        return sourceBookId;
    }

    public void setSourceBookId(String sourceBookId) {
        this.sourceBookId = sourceBookId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

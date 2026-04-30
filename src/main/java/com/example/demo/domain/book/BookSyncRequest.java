package com.example.demo.domain.book;

/**
 * 书籍同步请求对象。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
public class BookSyncRequest {

    private String keyword;
    private String language;
    private String topic;
    private Integer maxCount;
    private String triggerType;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
}

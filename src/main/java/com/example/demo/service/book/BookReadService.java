package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookReadInfo;
import com.example.demo.mapper.book.BookMapper;
import com.example.demo.util.HttpUtils;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 阅读服务，负责组装阅读页面所需的数据。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookReadService {

    private final BookMapper bookMapper;
    private final boolean mockEnabled;

    public BookReadService(BookMapper bookMapper, @Value("${bookhub.sync.mock-enabled:false}") boolean mockEnabled) {
        this.bookMapper = bookMapper;
        this.mockEnabled = mockEnabled;
    }

    public BookReadInfo getReadInfo(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null) {
            return null;
        }
        String readUrl = "EXTERNAL".equals(book.getReadMode()) && book.getExternalReadUrl() != null
                ? book.getExternalReadUrl() : book.getReadUrl();
        return new BookReadInfo(book.getId(), book.getTitle(), book.getReadMode(), book.getFileType(), readUrl);
    }

    public String getReadContent(Long id) {
        Book book = bookMapper.selectById(id);
        if (book == null || "EXTERNAL".equals(book.getReadMode()) || book.getReadUrl() == null) {
            return null;
        }
        if (mockEnabled) {
            return buildMockContent(book);
        }
        try {
            String content = HttpUtils.get(book.getReadUrl());
            if ("html".equalsIgnoreCase(book.getFileType())) {
                return ensureBaseHref(content, book.getReadUrl(), book.getTitle());
            }
            return content;
        } catch (IOException ex) {
            throw new IllegalStateException("读取书籍正文失败: " + book.getTitle(), ex);
        }
    }

    private String buildMockContent(Book book) {
        if ("html".equalsIgnoreCase(book.getFileType())) {
            return ensureBaseHref("<html><head><title>" + book.getTitle() + "</title></head>"
                    + "<body><article><h1>" + book.getTitle() + "</h1><p>"
                    + book.getDescription() + "</p><p>这是测试环境下的 HTML 阅读代理内容。</p></article></body></html>",
                    book.getReadUrl(), book.getTitle());
        }
        return book.getTitle() + "\n\n" + book.getDescription() + "\n\n这是测试环境下的 TXT 阅读代理内容。";
    }

    private String ensureBaseHref(String html, String readUrl, String title) {
        String safeHtml = html == null ? "" : html;
        String baseTag = "<base href=\"" + extractBaseUrl(readUrl) + "/\">";
        if (safeHtml.contains("<head")) {
            return safeHtml.replaceFirst("(?i)<head[^>]*>", "$0" + baseTag);
        }
        return "<html><head><meta charset=\"UTF-8\"><title>" + title + "</title>" + baseTag
                + "</head><body>" + safeHtml + "</body></html>";
    }

    private String extractBaseUrl(String readUrl) {
        int lastSlashIndex = readUrl == null ? -1 : readUrl.lastIndexOf('/');
        if (lastSlashIndex <= "https://".length()) {
            return readUrl == null ? "" : readUrl;
        }
        return readUrl.substring(0, lastSlashIndex);
    }
}

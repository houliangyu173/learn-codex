package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookSyncRequest;
import com.example.demo.util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Gutendex 真实采集服务。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class GutendexBookSyncService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookCategoryService bookCategoryService;

    @Value("${bookhub.sync.gutendex-base-url:https://gutendex.com}")
    private String gutendexBaseUrl;

    @Value("${bookhub.sync.default-max-count:10}")
    private int defaultMaxCount;

    @Value("${bookhub.sync.mock-enabled:false}")
    private boolean mockEnabled;

    public GutendexBookSyncService(BookCategoryService bookCategoryService) {
        this.bookCategoryService = bookCategoryService;
    }

    public List<Book> fetchBooks(BookSyncRequest request) {
        if (mockEnabled) {
            return buildMockBooks(request);
        }
        try {
            String response = HttpUtils.get(buildUrl(request));
            JsonNode root = objectMapper.readTree(response);
            JsonNode results = root.path("results");
            List<Book> books = new ArrayList<Book>();
            int maxCount = normalizeMaxCount(request.getMaxCount());
            for (JsonNode item : results) {
                if (books.size() >= maxCount) {
                    break;
                }
                Book book = convert(item, request);
                if (book != null) {
                    books.add(book);
                }
            }
            return books;
        } catch (Exception exception) {
            throw new IllegalStateException("调用 Gutendex 采集失败: " + exception.getMessage(), exception);
        }
    }

    private String buildUrl(BookSyncRequest request) {
        List<String> params = new ArrayList<String>();
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            params.add("search=" + encode(request.getKeyword().trim()));
        }
        if (request.getLanguage() != null && !request.getLanguage().trim().isEmpty()) {
            params.add("languages=" + encode(request.getLanguage().trim()));
        }
        if (request.getTopic() != null && !request.getTopic().trim().isEmpty()) {
            params.add("topic=" + encode(request.getTopic().trim()));
        }
        params.add("mime_type=" + encode("text/"));
        StringBuilder builder = new StringBuilder(gutendexBaseUrl).append("/books");
        if (!params.isEmpty()) {
            builder.append("?");
            for (int i = 0; i < params.size(); i++) {
                if (i > 0) {
                    builder.append("&");
                }
                builder.append(params.get(i));
            }
        }
        return builder.toString();
    }

    private Book convert(JsonNode item, BookSyncRequest request) {
        JsonNode formats = item.path("formats");
        String htmlUrl = firstFormatValue(formats, "text/html");
        String txtUrl = firstFormatValue(formats, "text/plain");
        String readUrl = htmlUrl != null ? htmlUrl : txtUrl;
        String fileType = htmlUrl != null ? "html" : (txtUrl != null ? "txt" : null);
        if (readUrl == null || fileType == null) {
            return null;
        }

        String title = item.path("title").asText("");
        String author = extractAuthor(item.path("authors"));
        String description = extractDescription(item.path("summaries"), item.path("subjects"));
        String tagText = buildTagText(item.path("subjects"), item.path("bookshelves"), request.getTopic());

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setCoverUrl(firstFormatValue(formats, "image/jpeg"));
        book.setSource("gutendex");
        book.setSourceBookId(item.path("id").asText());
        book.setLanguage(extractFirstText(item.path("languages")));
        book.setCategoryId(bookCategoryService.resolveCategoryId(
                title + " " + author + " " + description + " " + tagText));
        book.setFileType(fileType);
        book.setReadUrl(readUrl);
        book.setDownloadUrl(txtUrl != null ? txtUrl : readUrl);
        book.setStatus(1);
        return book;
    }

    private String extractAuthor(JsonNode authors) {
        if (authors != null && authors.isArray() && authors.size() > 0) {
            return authors.get(0).path("name").asText("未知作者");
        }
        return "未知作者";
    }

    private String extractDescription(JsonNode summaries, JsonNode subjects) {
        String summary = extractFirstText(summaries);
        if (summary != null && !summary.isEmpty()) {
            return summary;
        }
        return extractFirstText(subjects);
    }

    private String buildTagText(JsonNode subjects, JsonNode bookshelves, String topic) {
        StringBuilder builder = new StringBuilder();
        appendJsonArray(builder, subjects);
        appendJsonArray(builder, bookshelves);
        if (topic != null) {
            builder.append(" ").append(topic);
        }
        return builder.toString();
    }

    private void appendJsonArray(StringBuilder builder, JsonNode arrayNode) {
        if (arrayNode == null || !arrayNode.isArray()) {
            return;
        }
        for (JsonNode item : arrayNode) {
            builder.append(" ").append(item.asText(""));
        }
    }

    private String extractFirstText(JsonNode arrayNode) {
        if (arrayNode != null && arrayNode.isArray() && arrayNode.size() > 0) {
            return arrayNode.get(0).asText("");
        }
        return "";
    }

    private String firstFormatValue(JsonNode formats, String mimePrefix) {
        if (formats == null || !formats.isObject()) {
            return null;
        }
        Iterator<String> fieldNames = formats.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (fieldName.startsWith(mimePrefix)) {
                String value = formats.path(fieldName).asText(null);
                if (value != null && value.startsWith("http")) {
                    return value;
                }
            }
        }
        return null;
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception exception) {
            throw new IllegalStateException("URL 编码失败", exception);
        }
    }

    private int normalizeMaxCount(Integer maxCount) {
        if (maxCount == null || maxCount <= 0) {
            return defaultMaxCount;
        }
        return Math.min(maxCount, 32);
    }

    private List<Book> buildMockBooks(BookSyncRequest request) {
        List<Book> books = new ArrayList<Book>();
        String topic = request.getTopic() == null ? "children" : request.getTopic();

        Book first = new Book(null, "The Children of Odin", "Padraic Colum",
                "适合儿童阅读的北欧神话故事集，来自测试环境 mock 采集。",
                "https://dummyimage.com/300x420/f7e4c8/8d6242&text=Children",
                "gutendex", "24737", request.getLanguage() == null ? "en" : request.getLanguage(),
                bookCategoryService.resolveCategoryId("children mythology"), null, "html",
                "https://www.gutenberg.org/cache/epub/24737/pg24737-images.html",
                "https://www.gutenberg.org/files/24737/24737-0.txt", 1);
        books.add(first);

        Book second = new Book(null, "The History of Herodotus", "Herodotus",
                "用于测试环境的历史类 mock 采集结果。",
                "https://dummyimage.com/300x420/e3ddd0/7d6946&text=History",
                "gutendex", "2707", request.getLanguage() == null ? "en" : request.getLanguage(),
                bookCategoryService.resolveCategoryId(topic + " history"), null, "txt",
                "https://www.gutenberg.org/files/2707/2707-0.txt",
                "https://www.gutenberg.org/files/2707/2707-0.txt", 1);
        books.add(second);

        int maxCount = normalizeMaxCount(request.getMaxCount());
        return books.subList(0, Math.min(maxCount, books.size()));
    }
}

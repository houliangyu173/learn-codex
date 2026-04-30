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
 * Open Library 补充采集服务。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class OpenLibraryBookSyncService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookCategoryService bookCategoryService;

    @Value("${bookhub.sync.open-library-base-url:https://openlibrary.org}")
    private String openLibraryBaseUrl;

    @Value("${bookhub.sync.mock-enabled:false}")
    private boolean mockEnabled;

    public OpenLibraryBookSyncService(BookCategoryService bookCategoryService) {
        this.bookCategoryService = bookCategoryService;
    }

    public List<Book> fetchSupplementBooks(BookSyncRequest request, int limit) {
        if (limit <= 0) {
            return new ArrayList<Book>();
        }
        if (mockEnabled) {
            return buildMockBooks(request, limit);
        }
        try {
            String response = HttpUtils.get(buildSearchUrl(request, limit));
            JsonNode root = objectMapper.readTree(response);
            JsonNode docs = root.path("docs");
            List<Book> books = new ArrayList<Book>();
            for (JsonNode item : docs) {
                if (books.size() >= limit) {
                    break;
                }
                Book book = convert(item, request);
                if (book != null) {
                    books.add(book);
                }
            }
            return books;
        } catch (Exception exception) {
            throw new IllegalStateException("调用 Open Library 补充采集失败: " + exception.getMessage(), exception);
        }
    }

    public void enrichMissingFields(List<Book> books, BookSyncRequest request) {
        if (books == null || books.isEmpty()) {
            return;
        }
        for (Book book : books) {
            if (!needsEnrichment(book)) {
                continue;
            }
            Book enrichedBook = mockEnabled ? buildMockEnrichment(book) : fetchEnrichment(book, request);
            if (enrichedBook == null) {
                continue;
            }
            if (isBlank(book.getCoverUrl()) && !isBlank(enrichedBook.getCoverUrl())) {
                book.setCoverUrl(enrichedBook.getCoverUrl());
            }
            if (isBlank(book.getDescription()) && !isBlank(enrichedBook.getDescription())) {
                book.setDescription(enrichedBook.getDescription());
            }
        }
    }

    private Book fetchEnrichment(Book book, BookSyncRequest request) {
        try {
            String response = HttpUtils.get(buildEnrichmentUrl(book, request));
            JsonNode root = objectMapper.readTree(response);
            JsonNode docs = root.path("docs");
            if (!docs.isArray() || docs.size() == 0) {
                return null;
            }
            return convert(docs.get(0), request);
        } catch (Exception exception) {
            return null;
        }
    }

    private String buildSearchUrl(BookSyncRequest request, int limit) {
        StringBuilder queryBuilder = new StringBuilder();
        if (!isBlank(request.getKeyword())) {
            queryBuilder.append(request.getKeyword().trim());
        }
        if (!isBlank(request.getTopic())) {
            queryBuilder.append(" ").append(request.getTopic().trim());
        }
        if (queryBuilder.length() == 0) {
            queryBuilder.append("classic");
        }

        StringBuilder builder = new StringBuilder(openLibraryBaseUrl)
                .append("/search.json?q=").append(encode(queryBuilder.toString()))
                .append("&limit=").append(limit);
        if (!isBlank(request.getLanguage())) {
            builder.append("&language=").append(encode(request.getLanguage().trim()));
        }
        builder.append("&mode=everything");
        return builder.toString();
    }

    private String buildEnrichmentUrl(Book book, BookSyncRequest request) {
        StringBuilder queryBuilder = new StringBuilder(book.getTitle());
        if (!isBlank(book.getAuthor())) {
            queryBuilder.append(" ").append(book.getAuthor());
        }
        StringBuilder builder = new StringBuilder(openLibraryBaseUrl)
                .append("/search.json?q=").append(encode(queryBuilder.toString()))
                .append("&limit=1");
        if (!isBlank(request.getLanguage())) {
            builder.append("&language=").append(encode(request.getLanguage().trim()));
        }
        return builder.toString();
    }

    private Book convert(JsonNode item, BookSyncRequest request) {
        String editionKey = extractFirstText(item.path("edition_key"));
        if (isBlank(editionKey)) {
            editionKey = item.path("key").asText(null);
        }
        if (isBlank(editionKey)) {
            return null;
        }

        String title = item.path("title").asText("");
        String author = extractFirstText(item.path("author_name"));
        String description = extractDescription(item.path("first_sentence"), item.path("subject"));
        String readUrl = buildReadUrl(editionKey);
        String coverUrl = buildCoverUrl(item.path("cover_i"));

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(isBlank(author) ? "未知作者" : author);
        book.setDescription(description);
        book.setCoverUrl(coverUrl);
        book.setSource("open-library");
        book.setSourceBookId(editionKey);
        book.setLanguage(extractLanguage(item.path("language"), request.getLanguage()));
        book.setCategoryId(bookCategoryService.resolveCategoryId(
                title + " " + author + " " + description + " " + request.getTopic()));
        book.setReadMode("INTERNAL");
        book.setFileType("html");
        book.setReadUrl(readUrl);
        book.setDownloadUrl(readUrl);
        book.setStatus(1);
        return book;
    }

    private String extractDescription(JsonNode firstSentenceNode, JsonNode subjectNode) {
        String firstSentence = extractFirstText(firstSentenceNode);
        if (!isBlank(firstSentence)) {
            return firstSentence;
        }
        return extractFirstText(subjectNode);
    }

    private String extractLanguage(JsonNode languageNode, String requestLanguage) {
        String language = extractFirstText(languageNode);
        if (!isBlank(language)) {
            return language;
        }
        return isBlank(requestLanguage) ? "en" : requestLanguage;
    }

    private String extractFirstText(JsonNode arrayNode) {
        if (arrayNode != null && arrayNode.isArray() && arrayNode.size() > 0) {
            return arrayNode.get(0).asText("");
        }
        if (arrayNode != null && !arrayNode.isMissingNode() && !arrayNode.isNull()) {
            return arrayNode.asText("");
        }
        return "";
    }

    private String buildReadUrl(String editionKey) {
        String normalizedKey = editionKey.startsWith("/") ? editionKey.substring(editionKey.lastIndexOf("/") + 1) : editionKey;
        return openLibraryBaseUrl + "/books/" + normalizedKey;
    }

    private String buildCoverUrl(JsonNode coverNode) {
        if (coverNode == null || coverNode.isMissingNode() || coverNode.isNull()) {
            return null;
        }
        String coverId = coverNode.asText("");
        if (isBlank(coverId)) {
            return null;
        }
        return "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.name());
        } catch (Exception exception) {
            throw new IllegalStateException("URL 编码失败", exception);
        }
    }

    private boolean needsEnrichment(Book book) {
        return book != null && (isBlank(book.getCoverUrl()) || isBlank(book.getDescription()));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private List<Book> buildMockBooks(BookSyncRequest request, int limit) {
        List<Book> books = new ArrayList<Book>();

        Book first = new Book(null, "Open Library Science Atlas", "Open Library",
                "来自 Open Library mock 的科学类补充书籍。",
                "https://dummyimage.com/300x420/d6e2f0/59708a&text=Open+Sci",
                "open-library", "OLM1001M", request.getLanguage() == null ? "en" : request.getLanguage(),
                bookCategoryService.resolveCategoryId("science atlas"), null, "INTERNAL", "html",
                "https://openlibrary.org/books/OLM1001M",
                "https://openlibrary.org/books/OLM1001M", null, null, null, null, null, 1);
        books.add(first);

        Book second = new Book(null, "Open Library History Notes", "Open Library",
                "来自 Open Library mock 的历史类补充书籍。",
                "https://dummyimage.com/300x420/e3ddd0/7d6946&text=Open+History",
                "open-library", "OLM1002M", request.getLanguage() == null ? "en" : request.getLanguage(),
                bookCategoryService.resolveCategoryId("history notes"), null, "INTERNAL", "html",
                "https://openlibrary.org/books/OLM1002M",
                "https://openlibrary.org/books/OLM1002M", null, null, null, null, null, 1);
        books.add(second);

        return new ArrayList<Book>(books.subList(0, Math.min(limit, books.size())));
    }

    private Book buildMockEnrichment(Book book) {
        Book enrichedBook = new Book();
        enrichedBook.setCoverUrl("https://dummyimage.com/300x420/dae5ef/53687d&text=Open+Meta");
        enrichedBook.setDescription("由 Open Library mock 补充的简介信息。");
        return enrichedBook;
    }
}

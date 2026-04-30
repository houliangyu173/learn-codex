package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookSyncRequest;
import com.example.demo.domain.book.BookStatusUpdateRequest;
import com.example.demo.domain.book.BookSyncResult;
import com.example.demo.entity.BookSyncLogEntity;
import com.example.demo.mapper.book.BookMapper;
import com.example.demo.mapper.book.BookSyncLogEntityMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 后台书籍管理服务，负责同步和状态调整。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookAdminService {

    private static final int DEFAULT_MAX_COUNT = 10;
    private static final int MAX_SYNC_COUNT = 32;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BookMapper bookMapper;
    private final BookSyncLogEntityMapper bookSyncLogEntityMapper;
    private final GutendexBookSyncService gutendexBookSyncService;
    private final OpenLibraryBookSyncService openLibraryBookSyncService;
    private final SeventeenKRankingSyncService seventeenKRankingSyncService;

    public BookAdminService(BookMapper bookMapper, BookSyncLogEntityMapper bookSyncLogEntityMapper,
                            GutendexBookSyncService gutendexBookSyncService,
                            OpenLibraryBookSyncService openLibraryBookSyncService,
                            SeventeenKRankingSyncService seventeenKRankingSyncService) {
        this.bookMapper = bookMapper;
        this.bookSyncLogEntityMapper = bookSyncLogEntityMapper;
        this.gutendexBookSyncService = gutendexBookSyncService;
        this.openLibraryBookSyncService = openLibraryBookSyncService;
        this.seventeenKRankingSyncService = seventeenKRankingSyncService;
    }

    public BookSyncResult syncBooks(BookSyncRequest request) {
        BookSyncRequest normalizedRequest = normalizeRequest(request);
        LocalDateTime startTime = LocalDateTime.now();
        if ("17k".equalsIgnoreCase(normalizedRequest.getSourceChannel())) {
            return syncSeventeenKBooks(normalizedRequest, startTime);
        }
        try {
            List<Book> primaryBooks = gutendexBookSyncService.fetchBooks(normalizedRequest);
            openLibraryBookSyncService.enrichMissingFields(primaryBooks, normalizedRequest);

            int maxCount = normalizeMaxCount(normalizedRequest.getMaxCount());
            int remainingCount = Math.max(maxCount - primaryBooks.size(), 0);
            List<Book> supplementBooks = openLibraryBookSyncService.fetchSupplementBooks(normalizedRequest, remainingCount);

            int primarySuccessCount = bookMapper.batchSave(primaryBooks);
            int supplementSuccessCount = bookMapper.batchSave(supplementBooks);
            int successCount = primarySuccessCount + supplementSuccessCount;
            int failCount = Math.max(primaryBooks.size() - primarySuccessCount, 0)
                    + Math.max(supplementBooks.size() - supplementSuccessCount, 0);
            String message = buildSuccessMessage(supplementSuccessCount);
            saveSyncLog(startTime, normalizedRequest, successCount, failCount, "SUCCESS", message, null);

            BookSyncResult result = new BookSyncResult("gutendex", successCount, failCount, "同步完成");
            result.setSupplementSource("open-library");
            result.setSupplementCount(supplementSuccessCount);
            return result;
        } catch (Exception exception) {
            saveSyncLog(startTime, normalizedRequest, 0, 1, "FAILED", "同步失败", exception.getMessage());
            return new BookSyncResult("gutendex", 0, 1, "同步失败");
        }
    }

    private BookSyncResult syncSeventeenKBooks(BookSyncRequest request, LocalDateTime startTime) {
        try {
            List<Book> books = seventeenKRankingSyncService.fetchRankingBooks(request);
            int successCount = bookMapper.batchSave(books);
            int failCount = Math.max(books.size() - successCount, 0);
            String message = buildSeventeenKMessage(request, successCount);
            saveSyncLog(startTime, request, successCount, failCount, "SUCCESS", message, null);
            return new BookSyncResult("17k", successCount, failCount, "同步完成");
        } catch (Exception exception) {
            saveSyncLog(startTime, request, 0, 1, "FAILED", "同步失败", exception.getMessage());
            return new BookSyncResult("17k", 0, 1, "同步失败");
        }
    }

    public Book updateStatus(BookStatusUpdateRequest request) {
        return bookMapper.updateStatus(request.getId(), request.getStatus());
    }

    private void saveSyncLog(LocalDateTime startTime, BookSyncRequest request, int successCount, int failCount,
                             String status, String message, String errorMessage) {
        BookSyncLogEntity logEntity = new BookSyncLogEntity();
        logEntity.setSource(request.getSourceChannel());
        logEntity.setTriggerType(request.getTriggerType());
        logEntity.setRequestParams(toJson(request));
        logEntity.setSuccessCount(successCount);
        logEntity.setFailCount(failCount);
        logEntity.setStatus(status);
        logEntity.setMessage(message);
        logEntity.setErrorMessage(errorMessage);
        logEntity.setStartTime(startTime);
        logEntity.setEndTime(LocalDateTime.now());
        bookSyncLogEntityMapper.insert(logEntity);
    }

    private BookSyncRequest normalizeRequest(BookSyncRequest request) {
        BookSyncRequest normalizedRequest = request == null ? new BookSyncRequest() : request;
        if (normalizedRequest.getTriggerType() == null || normalizedRequest.getTriggerType().trim().isEmpty()) {
            normalizedRequest.setTriggerType("MANUAL");
        }
        if (normalizedRequest.getSourceChannel() == null || normalizedRequest.getSourceChannel().trim().isEmpty()) {
            normalizedRequest.setSourceChannel("gutendex");
        }
        return normalizedRequest;
    }

    private int normalizeMaxCount(Integer maxCount) {
        if (maxCount == null || maxCount <= 0) {
            return DEFAULT_MAX_COUNT;
        }
        return Math.min(maxCount, MAX_SYNC_COUNT);
    }

    private String buildSuccessMessage(int supplementSuccessCount) {
        if (supplementSuccessCount > 0) {
            return "同步完成，Open Library 补充 " + supplementSuccessCount + " 条";
        }
        return "同步完成";
    }

    private String buildSeventeenKMessage(BookSyncRequest request, int successCount) {
        String rankType = request.getRankType();
        if ("17k_female_click".equals(rankType)) {
            return "同步完成，17K 女生作品点击榜导入 " + successCount + " 条";
        }
        return "同步完成，17K 男生作品点击榜导入 " + successCount + " 条";
    }

    private String toJson(BookSyncRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception exception) {
            return "{}";
        }
    }
}

package com.example.demo.controller.admin;

import com.example.demo.common.ApiResponse;
import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookSyncLogQuery;
import com.example.demo.domain.book.BookSyncRequest;
import com.example.demo.domain.book.BookStatusUpdateRequest;
import com.example.demo.domain.book.BookSyncResult;
import com.example.demo.domain.book.BookSyncLog;
import com.example.demo.domain.book.PageResult;
import com.example.demo.service.book.BookAdminService;
import com.example.demo.service.book.BookSyncLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台书籍管理控制器。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@RestController
@RequestMapping("/admin/book")
public class AdminBookController {

    private final BookAdminService bookAdminService;
    private final BookSyncLogService bookSyncLogService;

    public AdminBookController(BookAdminService bookAdminService, BookSyncLogService bookSyncLogService) {
        this.bookAdminService = bookAdminService;
        this.bookSyncLogService = bookSyncLogService;
    }

    @PostMapping("/sync")
    public ApiResponse<BookSyncResult> sync(@RequestBody(required = false) BookSyncRequest request) {
        return ApiResponse.success(bookAdminService.syncBooks(request));
    }

    @PutMapping("/status")
    public ApiResponse<Book> updateStatus(@RequestBody BookStatusUpdateRequest request) {
        return ApiResponse.success(bookAdminService.updateStatus(request));
    }

    @GetMapping("/sync/log/list")
    public ApiResponse<PageResult<BookSyncLog>> syncLogList(
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(value = "source", required = false) String source,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "triggerType", required = false) String triggerType) {
        BookSyncLogQuery query = new BookSyncLogQuery();
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);
        query.setSource(source);
        query.setStatus(status);
        query.setTriggerType(triggerType);
        return ApiResponse.success(bookSyncLogService.queryPage(query));
    }
}

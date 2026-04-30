package com.example.demo.controller.book;

import com.example.demo.common.ApiResponse;
import com.example.demo.domain.book.BookshelfAddRequest;
import com.example.demo.domain.book.BookshelfItem;
import com.example.demo.domain.book.BookshelfProgressUpdateRequest;
import com.example.demo.domain.book.PageResult;
import com.example.demo.service.book.BookshelfService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户书架接口控制器。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@RestController
@RequestMapping("/bookshelf")
public class BookshelfController {

    private final BookshelfService bookshelfService;

    public BookshelfController(BookshelfService bookshelfService) {
        this.bookshelfService = bookshelfService;
    }

    @PostMapping("/add")
    public ApiResponse<BookshelfItem> add(@RequestBody BookshelfAddRequest request) {
        return ApiResponse.success(bookshelfService.add(request));
    }

    @PutMapping("/progress")
    public ApiResponse<BookshelfItem> updateProgress(@RequestBody BookshelfProgressUpdateRequest request) {
        return ApiResponse.success(bookshelfService.updateProgress(request));
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<BookshelfItem>> list(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ApiResponse.success(bookshelfService.queryPage(userId, pageNum, pageSize));
    }
}

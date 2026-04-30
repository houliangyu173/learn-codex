package com.example.demo.controller.book;

import com.example.demo.common.ApiResponse;
import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookCategory;
import com.example.demo.domain.book.BookQueryCondition;
import com.example.demo.domain.book.BookReadInfo;
import com.example.demo.domain.book.PageResult;
import com.example.demo.service.book.BookCategoryService;
import com.example.demo.service.book.BookQueryService;
import com.example.demo.service.book.BookReadService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台书籍接口控制器。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@RestController
@RequestMapping("/book")
public class BookController {

    private final BookQueryService bookQueryService;
    private final BookReadService bookReadService;
    private final BookCategoryService bookCategoryService;

    public BookController(BookQueryService bookQueryService, BookReadService bookReadService,
                          BookCategoryService bookCategoryService) {
        this.bookQueryService = bookQueryService;
        this.bookReadService = bookReadService;
        this.bookCategoryService = bookCategoryService;
    }

    @GetMapping("/list")
    public ApiResponse<PageResult<Book>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                              @RequestParam(value = "categoryId", required = false) Long categoryId,
                                              @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        BookQueryCondition condition = new BookQueryCondition();
        condition.setKeyword(keyword);
        condition.setCategoryId(categoryId);
        condition.setPageNum(pageNum);
        condition.setPageSize(pageSize);
        return ApiResponse.success(bookQueryService.queryPage(condition));
    }

    @GetMapping("/{id}")
    public ApiResponse<Book> detail(@PathVariable("id") Long id) {
        return ApiResponse.success(bookQueryService.getDetail(id));
    }

    @GetMapping("/read/{id}")
    public ApiResponse<BookReadInfo> read(@PathVariable("id") Long id) {
        return ApiResponse.success(bookReadService.getReadInfo(id));
    }

    @GetMapping("/read/content/{id}")
    public ApiResponse<String> readContent(@PathVariable("id") Long id) {
        return ApiResponse.success(bookReadService.getReadContent(id));
    }

    @GetMapping("/category/list")
    public ApiResponse<List<BookCategory>> categoryList() {
        return ApiResponse.success(bookCategoryService.listEnabledCategories());
    }
}

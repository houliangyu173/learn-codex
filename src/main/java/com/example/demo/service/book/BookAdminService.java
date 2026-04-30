package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookStatusUpdateRequest;
import com.example.demo.domain.book.BookSyncResult;
import com.example.demo.entity.BookSyncLogEntity;
import com.example.demo.mapper.book.BookMapper;
import com.example.demo.mapper.book.BookSyncLogEntityMapper;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * 后台书籍管理服务，负责同步和状态调整。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class BookAdminService {

    private final BookMapper bookMapper;
    private final BookSyncLogEntityMapper bookSyncLogEntityMapper;

    public BookAdminService(BookMapper bookMapper, BookSyncLogEntityMapper bookSyncLogEntityMapper) {
        this.bookMapper = bookMapper;
        this.bookSyncLogEntityMapper = bookSyncLogEntityMapper;
    }

    public BookSyncResult syncBooks() {
        List<Book> importedBooks = new ArrayList<Book>();
        importedBooks.add(new Book(null, "The Children of Odin", "Padraic Colum",
                "适合儿童阅读的北欧神话故事集，用于演示儿童分类。",
                "https://dummyimage.com/300x420/f7e4c8/8d6242&text=Children",
                "gutendex", "24737", "en", 5L, "儿童", "html",
                "https://www.gutenberg.org/cache/epub/24737/pg24737-images.html",
                "https://www.gutenberg.org/files/24737/24737-0.txt", 1));
        importedBooks.add(new Book(null, "Computers and People", "Anonymous",
                "用于演示技术分类的样例书籍。",
                "https://dummyimage.com/300x420/d8e7db/5f7b67&text=Tech",
                "gutendex", "99001", "en", 4L, "技术", "txt",
                "https://www.gutenberg.org/files/98/98-0.txt",
                "https://www.gutenberg.org/files/98/98-0.txt", 1));

        // 这里先用内存数据模拟采集结果，保留后续切换真实 API 的编排入口。
        int successCount = bookMapper.batchSave(importedBooks);
        saveSyncLog(successCount, importedBooks.size() - successCount);
        return new BookSyncResult(successCount, "同步完成");
    }

    public Book updateStatus(BookStatusUpdateRequest request) {
        return bookMapper.updateStatus(request.getId(), request.getStatus());
    }

    private void saveSyncLog(int successCount, int failCount) {
        BookSyncLogEntity logEntity = new BookSyncLogEntity();
        logEntity.setSource("gutendex");
        logEntity.setTriggerType("MANUAL");
        logEntity.setRequestParams("{}");
        logEntity.setSuccessCount(successCount);
        logEntity.setFailCount(failCount);
        logEntity.setStatus(failCount > 0 ? "PARTIAL_SUCCESS" : "SUCCESS");
        logEntity.setMessage("同步完成");
        logEntity.setStartTime(LocalDateTime.now());
        logEntity.setEndTime(LocalDateTime.now());
        bookSyncLogEntityMapper.insert(logEntity);
    }
}

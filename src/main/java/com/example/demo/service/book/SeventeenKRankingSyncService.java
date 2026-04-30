package com.example.demo.service.book;

import com.example.demo.domain.book.Book;
import com.example.demo.domain.book.BookSyncRequest;
import com.example.demo.util.HttpUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 17K 榜单采集服务。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Service
public class SeventeenKRankingSyncService {

    private static final Pattern MALE_PATTERN = Pattern.compile(
            "<a[^>]*href=\"(https://www\\.17k\\.com/book/[^\"#?]+)\"[^>]*>([^<]+)</a>",
            Pattern.CASE_INSENSITIVE);

    private final BookCategoryService bookCategoryService;

    @Value("${bookhub.sync.17k-base-url:https://www.17k.com}")
    private String seventeenKBaseUrl;

    @Value("${bookhub.sync.mock-enabled:false}")
    private boolean mockEnabled;

    public SeventeenKRankingSyncService(BookCategoryService bookCategoryService) {
        this.bookCategoryService = bookCategoryService;
    }

    public List<Book> fetchRankingBooks(BookSyncRequest request) {
        int maxCount = normalizeMaxCount(request.getMaxCount());
        if (mockEnabled) {
            return buildMockBooks(request, maxCount);
        }
        try {
            String html = HttpUtils.get(seventeenKBaseUrl + "/");
            return parseRankingBooks(html, request, maxCount);
        } catch (Exception exception) {
            throw new IllegalStateException("调用 17K 榜单采集失败: " + exception.getMessage(), exception);
        }
    }

    private List<Book> parseRankingBooks(String html, BookSyncRequest request, int maxCount) {
        List<Book> books = new ArrayList<Book>();
        Matcher matcher = MALE_PATTERN.matcher(html);
        int rankNo = 1;
        while (matcher.find() && books.size() < maxCount) {
            String url = matcher.group(1);
            String title = matcher.group(2).trim();
            if (title.isEmpty()) {
                continue;
            }
            if (booksContainTitle(books, title)) {
                continue;
            }
            books.add(buildExternalBook(
                    title,
                    "17K小说网",
                    request.getRankType(),
                    rankNo,
                    0L,
                    url,
                    "来自 17K 榜单采集的作品，详情信息待进一步补充。"));
            rankNo++;
        }
        return books;
    }

    private boolean booksContainTitle(List<Book> books, String title) {
        for (Book book : books) {
            if (title.equals(book.getTitle())) {
                return true;
            }
        }
        return false;
    }

    private List<Book> buildMockBooks(BookSyncRequest request, int maxCount) {
        List<Book> books = new ArrayList<Book>();
        String rankType = request.getRankType() == null ? "17k_male_click" : request.getRankType();
        boolean female = "17k_female_click".equals(rankType);

        if (female) {
            books.add(buildExternalBook(
                    "说好游戏直播，弹幕却说我是天选女主",
                    "17K女生",
                    rankType,
                    1,
                    90321L,
                    "https://www.17k.com/book/400001.html",
                    "17K 女生作品点击榜 mock 榜首作品。"));
            books.add(buildExternalBook(
                    "离婚后，被装乖奶狗缠上了",
                    "17K女生",
                    rankType,
                    2,
                    82500L,
                    "https://www.17k.com/book/400002.html",
                    "17K 女生作品点击榜 mock 榜单作品。"));
        } else {
            books.add(buildExternalBook(
                    "重生倚天张无忌的长生之路",
                    "17K男生",
                    rankType,
                    1,
                    140234L,
                    "https://www.17k.com/book/400101.html",
                    "17K 男生作品点击榜 mock 榜首作品。"));
            books.add(buildExternalBook(
                    "修罗武神",
                    "17K男生",
                    rankType,
                    2,
                    74526L,
                    "https://www.17k.com/book/400102.html",
                    "17K 男生作品点击榜 mock 榜单作品。"));
            books.add(buildExternalBook(
                    "分身三地十年约",
                    "17K男生",
                    rankType,
                    3,
                    20593L,
                    "https://www.17k.com/book/400103.html",
                    "17K 男生作品点击榜 mock 榜单作品。"));
        }
        return new ArrayList<Book>(books.subList(0, Math.min(maxCount, books.size())));
    }

    private Book buildExternalBook(String title, String author, String rankType, int rankNo, long rankValue,
                                   String detailUrl, String description) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setCoverUrl("https://dummyimage.com/300x420/e8d7c4/6d4c41&text=17K");
        book.setSource("17k");
        book.setSourceBookId(extractSourceBookId(detailUrl, rankType, rankNo));
        book.setLanguage("zh");
        book.setCategoryId(bookCategoryService.resolveCategoryId(title + " " + description));
        book.setReadMode("EXTERNAL");
        book.setFileType("html");
        book.setReadUrl(detailUrl);
        book.setDownloadUrl(detailUrl);
        book.setExternalDetailUrl(detailUrl);
        book.setExternalReadUrl(detailUrl);
        book.setRankType(rankType);
        book.setRankNo(rankNo);
        book.setRankValue(rankValue);
        book.setStatus(1);
        return book;
    }

    private String extractSourceBookId(String detailUrl, String rankType, int rankNo) {
        Matcher matcher = Pattern.compile("/book/([0-9]+)\\.html").matcher(detailUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return rankType + "-" + rankNo;
    }

    private int normalizeMaxCount(Integer maxCount) {
        if (maxCount == null || maxCount <= 0) {
            return 10;
        }
        return Math.min(maxCount, 20);
    }
}

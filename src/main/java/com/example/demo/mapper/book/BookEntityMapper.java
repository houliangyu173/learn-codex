package com.example.demo.mapper.book;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.BookEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * MyBatis-Plus 书籍实体 Mapper。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Mapper
public interface BookEntityMapper extends BaseMapper<BookEntity> {

    @Select({
            "<script>",
            "SELECT b.id, b.title, b.author, b.description, b.cover_url AS coverUrl,",
            "b.source, b.source_book_id AS sourceBookId, b.language, b.category_id AS categoryId,",
            "c.name AS categoryName, b.read_mode AS readMode, b.file_type AS fileType, b.read_url AS readUrl,",
            "b.download_url AS downloadUrl, b.external_detail_url AS externalDetailUrl,",
            "b.external_read_url AS externalReadUrl, b.rank_type AS rankType, b.rank_no AS rankNo,",
            "b.rank_value AS rankValue, b.status, b.sync_status AS syncStatus,",
            "b.create_time AS createTime, b.update_time AS updateTime",
            "FROM book b",
            "LEFT JOIN book_category c ON b.category_id = c.id",
            "WHERE 1 = 1",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (b.title LIKE CONCAT('%', #{keyword}, '%') OR b.author LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "<if test='categoryId != null'>",
            "AND b.category_id = #{categoryId}",
            "</if>",
            "ORDER BY b.id ASC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<BookEntity> selectPageWithCategory(@Param("keyword") String keyword,
                                            @Param("categoryId") Long categoryId,
                                            @Param("offset") long offset,
                                            @Param("pageSize") int pageSize);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM book b",
            "WHERE 1 = 1",
            "<if test='keyword != null and keyword != \"\"'>",
            "AND (b.title LIKE CONCAT('%', #{keyword}, '%') OR b.author LIKE CONCAT('%', #{keyword}, '%'))",
            "</if>",
            "<if test='categoryId != null'>",
            "AND b.category_id = #{categoryId}",
            "</if>",
            "</script>"
    })
    long countPage(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    @Select("SELECT b.id, b.title, b.author, b.description, b.cover_url AS coverUrl, "
            + "b.source, b.source_book_id AS sourceBookId, b.language, b.category_id AS categoryId, "
            + "c.name AS categoryName, b.read_mode AS readMode, b.file_type AS fileType, b.read_url AS readUrl, "
            + "b.download_url AS downloadUrl, b.external_detail_url AS externalDetailUrl, "
            + "b.external_read_url AS externalReadUrl, b.rank_type AS rankType, b.rank_no AS rankNo, "
            + "b.rank_value AS rankValue, b.status, b.sync_status AS syncStatus, "
            + "b.create_time AS createTime, b.update_time AS updateTime "
            + "FROM book b LEFT JOIN book_category c ON b.category_id = c.id WHERE b.id = #{id}")
    BookEntity selectDetailById(@Param("id") Long id);
}

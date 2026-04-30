package com.example.demo.mapper.book;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.entity.UserBookshelfEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户书架实体 Mapper。
 *
 * @author houliangyu
 * @since 2026-04-30
 */
@Mapper
public interface UserBookshelfEntityMapper extends BaseMapper<UserBookshelfEntity> {

    @Select({
            "<script>",
            "SELECT s.id, s.user_id AS userId, s.book_id AS bookId, s.read_progress AS readProgress,",
            "s.last_read_time AS lastReadTime, s.create_time AS createTime, s.update_time AS updateTime,",
            "b.title, b.author, b.cover_url AS coverUrl, c.name AS categoryName",
            "FROM user_bookshelf s",
            "INNER JOIN book b ON s.book_id = b.id",
            "LEFT JOIN book_category c ON b.category_id = c.id",
            "WHERE s.user_id = #{userId}",
            "ORDER BY s.last_read_time DESC, s.id DESC",
            "LIMIT #{offset}, #{pageSize}",
            "</script>"
    })
    List<UserBookshelfEntity> selectPageByUserId(@Param("userId") Long userId,
                                                 @Param("offset") long offset,
                                                 @Param("pageSize") int pageSize);
}

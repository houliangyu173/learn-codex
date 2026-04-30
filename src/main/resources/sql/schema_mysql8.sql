SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `user_bookshelf`;
DROP TABLE IF EXISTS `book_sync_log`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `book_category`;

CREATE TABLE `book_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '分类名称',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父级分类ID，0表示顶级分类',
  `sort` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越小越靠前',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_category_parent_id` (`parent_id`),
  KEY `idx_book_category_status_sort` (`status`, `sort`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='书籍分类表';

CREATE TABLE `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(255) NOT NULL COMMENT '书名',
  `author` VARCHAR(255) DEFAULT NULL COMMENT '作者',
  `description` TEXT COMMENT '简介',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面地址',
  `source` VARCHAR(50) NOT NULL COMMENT '数据来源，如 gutendex/open-library',
  `source_book_id` VARCHAR(100) NOT NULL COMMENT '来源系统中的书籍编号',
  `language` VARCHAR(20) DEFAULT NULL COMMENT '语言',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `read_mode` VARCHAR(20) NOT NULL DEFAULT 'INTERNAL' COMMENT '阅读模式：INTERNAL/EXTERNAL',
  `file_type` VARCHAR(20) DEFAULT NULL COMMENT '阅读文件类型，如 html/txt',
  `read_url` VARCHAR(500) DEFAULT NULL COMMENT '在线阅读地址',
  `download_url` VARCHAR(500) DEFAULT NULL COMMENT '下载地址',
  `external_detail_url` VARCHAR(500) DEFAULT NULL COMMENT '外部详情页地址',
  `external_read_url` VARCHAR(500) DEFAULT NULL COMMENT '外部阅读地址',
  `rank_type` VARCHAR(50) DEFAULT NULL COMMENT '榜单类型',
  `rank_no` INT DEFAULT NULL COMMENT '榜单排名',
  `rank_value` BIGINT DEFAULT NULL COMMENT '榜单值',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '上架状态：0下架，1上架',
  `sync_status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS' COMMENT '同步状态：SUCCESS/PARTIAL_SUCCESS/FAILED',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_source_source_book_id` (`source`, `source_book_id`),
  KEY `idx_book_category_id` (`category_id`),
  KEY `idx_book_status` (`status`),
  KEY `idx_book_language` (`language`),
  KEY `idx_book_title` (`title`),
  CONSTRAINT `fk_book_category_id` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='书籍表';

CREATE TABLE `user_bookshelf` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `read_progress` INT NOT NULL DEFAULT 0 COMMENT '阅读进度，V1按百分比或页码扩展',
  `last_read_time` DATETIME DEFAULT NULL COMMENT '最后阅读时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_bookshelf_user_book` (`user_id`, `book_id`),
  KEY `idx_user_bookshelf_book_id` (`book_id`),
  KEY `idx_user_bookshelf_last_read_time` (`last_read_time`),
  CONSTRAINT `fk_user_bookshelf_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户书架表';

CREATE TABLE `book_sync_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source` VARCHAR(50) NOT NULL COMMENT '同步来源',
  `trigger_type` VARCHAR(30) NOT NULL DEFAULT 'MANUAL' COMMENT '触发方式：MANUAL/SCHEDULED',
  `request_params` VARCHAR(1000) DEFAULT NULL COMMENT '同步请求参数JSON或文本',
  `success_count` INT NOT NULL DEFAULT 0 COMMENT '成功数量',
  `fail_count` INT NOT NULL DEFAULT 0 COMMENT '失败数量',
  `status` VARCHAR(30) NOT NULL DEFAULT 'SUCCESS' COMMENT '执行状态：SUCCESS/PARTIAL_SUCCESS/FAILED',
  `message` VARCHAR(500) DEFAULT NULL COMMENT '结果说明',
  `error_message` VARCHAR(2000) DEFAULT NULL COMMENT '错误信息',
  `start_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  `end_time` DATETIME DEFAULT NULL COMMENT '结束时间',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_sync_log_source` (`source`),
  KEY `idx_book_sync_log_status` (`status`),
  KEY `idx_book_sync_log_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='书籍同步日志表';

SET FOREIGN_KEY_CHECKS = 1;

DELETE FROM `user_bookshelf`;
DELETE FROM `book_sync_log`;
DELETE FROM `book`;
DELETE FROM `book_category`;

INSERT INTO `book_category` (`id`, `name`, `parent_id`, `sort`, `status`)
VALUES
  (1, '科学', 0, 10, 1),
  (2, '历史', 0, 20, 1),
  (3, '小说', 0, 30, 1),
  (4, '技术', 0, 40, 1),
  (5, '儿童', 0, 50, 1);

INSERT INTO `book` (`id`, `title`, `author`, `description`, `cover_url`, `source`, `source_book_id`,
                    `language`, `category_id`, `file_type`, `read_url`, `download_url`, `status`, `sync_status`)
VALUES
  (1, 'Pride and Prejudice', 'Jane Austen', '一部经典的英国小说，围绕爱情、偏见与成长展开。',
   'https://www.gutenberg.org/cache/epub/1342/pg1342.cover.medium.jpg', 'gutendex', '1342',
   'en', 3, 'html', 'https://www.gutenberg.org/cache/epub/1342/pg1342-images.html',
   'https://www.gutenberg.org/files/1342/1342-0.txt', 1, 'SUCCESS'),
  (2, 'A Brief History of Time', 'Stephen Hawking', '以通俗方式介绍宇宙学和时间概念的科普作品。',
   'https://dummyimage.com/300x420/d6e2f0/59708a&text=Science', 'open-library', 'OL82563M',
   'en', 1, 'txt', 'https://www.gutenberg.org/files/11/11-0.txt',
   'https://www.gutenberg.org/files/11/11-0.txt', 1, 'SUCCESS'),
  (3, 'The History of the Peloponnesian War', 'Thucydides', '古希腊历史名著，记录伯罗奔尼撒战争全貌。',
   'https://dummyimage.com/300x420/e3ddd0/7d6946&text=History', 'gutendex', '7142',
   'en', 2, 'html', 'https://www.gutenberg.org/cache/epub/7142/pg7142-images.html',
   'https://www.gutenberg.org/files/7142/7142-0.txt', 1, 'SUCCESS');

INSERT INTO `book_sync_log` (`id`, `source`, `trigger_type`, `request_params`, `success_count`, `fail_count`,
                             `status`, `message`)
VALUES
  (1, 'gutendex', 'MANUAL', '{"keyword":"classic"}', 3, 0, 'SUCCESS', '初始化数据导入完成');

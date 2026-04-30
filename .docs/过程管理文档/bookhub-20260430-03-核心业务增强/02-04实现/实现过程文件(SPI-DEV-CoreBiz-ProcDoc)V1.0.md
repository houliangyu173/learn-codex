---
项目: BookHub-在线电子书阅读平台
作者: houliangyu
日期: 2026-04-30
编号: SPI-DEV-CoreBiz-ProcDoc
阶段: 02-04实现，DEV
状态: 已基线
---

# 实现过程文件

## 1. 当前实现策略

按以下顺序推进：

1. 真实采集流程
2. 书架功能
3. 后台日志增强

## 2. 本轮实际实现内容

1. 后端新增 `BookSyncRequest`、`BookSyncLogQuery`、`BookshelfItem` 等领域对象。
2. 后端新增 `GutendexBookSyncService` 与 `OpenLibraryBookSyncService`，默认接入真实采集，测试环境通过配置切换到 mock 采集。
3. 后端新增 `BookshelfController`、`BookshelfService`、`UserBookshelfEntity` 与 Mapper，实现加入书架、分页列表、更新进度。
4. 后端增强 `BookAdminService` 和 `BookSyncLogService`，记录请求参数、错误信息，并支持日志筛选。
5. 前端新增 [Bookshelf.vue](/Users/houliangyu/myproject/codex-clound/bookhub-web/src/views/Bookshelf.vue)，首页增加书架入口，详情页增加“加入书架”，阅读页自动回写基础进度。
6. 后台页补充同步条件输入和日志筛选展示，开发代理增加 `/bookshelf` 转发。
7. 本轮新增 Open Library 补充采集编排：数量不足时补新书，字段缺失时补简介/封面，同步结果和日志增加补充来源信息。
8. 本轮新增 `SeventeenKRankingSyncService`，实现 17K 榜单来源 mock 采集与生产环境 HTML 解析骨架。
9. 数据模型扩展出 `readMode`、外部链接、榜单类型、榜单排名和榜单值字段。
10. 前端后台同步区新增 17K 榜单入口，详情页和阅读页支持 `EXTERNAL` 站外阅读模式。
11. 本轮修复站内阅读空白问题：后端新增 `/book/read/content/{id}` 正文代理接口，`BookReadService` 负责服务端拉取 `txt/html` 内容。
12. 前端阅读页改为请求本站正文代理接口，`html` 通过 `iframe srcdoc` 渲染，`txt` 不再直接跨域读取外部链接。

## 3. 验证记录

1. `mvn -Dmaven.repo.local=/tmp/codex-m2 -Dtest=BookHubControllerTest test`：阅读代理相关接口测试通过。
2. `mvn -Dmaven.repo.local=/tmp/codex-m2 test`：24 个测试通过。
3. `cd bookhub-web && npm run build`：构建通过，仅存在 Vue2 供应商包体积告警。

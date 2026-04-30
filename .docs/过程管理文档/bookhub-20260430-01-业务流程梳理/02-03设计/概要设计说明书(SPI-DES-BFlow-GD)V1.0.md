---
项目: BookHub-在线电子书阅读平台
作者: houliangyu
日期: 2026-04-30
编号: SPI-DES-BFlow-GD
阶段: 02-03设计，DES
状态: 草案
---

# 概要设计说明书

## 1. 设计目标

基于需求规格，对 BookHub V1 的模块边界、处理流程、分层结构和后续代码骨架方向进行定义，确保后续实现符合既定架构规范。

## 2. 总体架构

系统采用标准后端分层架构：

`controller -> service -> domain -> mapper -> database`

各层职责如下：

1. `controller` 负责接口暴露、参数接收与返回封装。
2. `service` 负责业务编排与事务控制。
3. `domain` 负责领域对象、参数对象、结果对象和业务规则承载。
4. `mapper` 负责数据库访问与实体持久化。
5. `database` 负责书籍、分类、书架、日志等数据存储。

## 3. 模块划分

### 3.1 书籍采集模块

职责：

1. 访问 Gutendex / Open Library 外部接口。
2. 解析外部 JSON 结构。
3. 将外部结构转换为内部统一书籍数据模型。
4. 执行去重与入库。

建议子职责拆分：

1. 外部 API Client
2. 数据转换器 Converter
3. 同步服务 SyncService
4. 同步日志服务 SyncLogService

### 3.2 自动分类模块

职责：

1. 接收标准化后的书籍信息。
2. 从标题、主题、书架标签、描述中提取文本特征。
3. 按规则映射到内部分类。
4. 输出分类结果并回写书籍数据。

建议子职责拆分：

1. 分类规则配置
2. 分类匹配器
3. 分类应用服务

### 3.3 书籍管理模块

职责：

1. 提供书籍分页列表查询。
2. 提供书籍详情查询。
3. 提供分类和状态条件过滤。

建议子职责拆分：

1. 查询服务
2. 详情服务
3. 数据展示对象组装

### 3.4 阅读模块

职责：

1. 根据书籍资源类型决定阅读方式。
2. 向前端返回 TXT / HTML 阅读地址。
3. 为书架进度记录提供基础阅读上下文。

### 3.5 后台管理模块

职责：

1. 提供管理员同步入口。
2. 提供采集日志查询。
3. 提供书籍上下架控制。

## 4. 核心处理流程设计

### 4.1 采集与入库流程

1. `AdminBookController` 接收 `/admin/book/sync` 请求。
2. `BookSyncService` 根据条件调用外部客户端。
3. `GutendexClient` / `OpenLibraryClient` 请求外部接口。
4. `BookDataConverter` 将响应转换为内部 `BookAggregate` 或 `BookImportCommand`。
5. `BookService` 调用去重逻辑判断是否存在相同来源书籍。
6. `BookCategoryService` 对书籍文本信息进行分类。
7. `BookDomainService` 完成实体构建和持久化。
8. `BookSyncLogService` 记录同步结果。

### 4.2 前台查询流程

1. `BookController` 接收 `/book/list` 请求。
2. `BookQueryService` 根据关键字、分类、语言、状态组装查询条件。
3. `BookMapper` 执行分页查询。
4. `BookAssembler` 将实体转为列表 VO。
5. 系统返回分页数据。

### 4.3 详情与阅读流程

1. `BookController` 接收 `/book/{id}` 请求，返回详情。
2. `BookReadController` 接收 `/book/read/{id}` 请求。
3. `BookReadService` 根据 `fileType` 和 `readUrl` 判断阅读方式。
4. 若可读资源存在，则返回阅读结果或跳转地址。

## 5. 数据模型建议

### 5.1 领域实体

1. `BookEntity`：对应书籍主表。
2. `BookCategoryEntity`：对应分类表。
3. `UserBookshelfEntity`：对应用户书架表。
4. `BookSyncLogEntity`：对应同步日志表。

### 5.2 领域对象

1. `BookImportCommand`：采集入库命令对象。
2. `BookQueryCondition`：书籍查询条件对象。
3. `BookReadResult`：阅读结果对象。
4. `BookCategoryMatchResult`：分类匹配结果对象。

### 5.3 展示对象

1. `BookListVO`：书籍列表展示对象。
2. `BookDetailVO`：书籍详情展示对象。
3. `BookReadVO`：阅读返回对象。
4. `BookSyncLogVO`：同步日志展示对象。

## 6. 状态设计

### 6.1 书籍状态

1. `0`：下架
2. `1`：上架

### 6.2 同步状态

1. `SUCCESS`：同步成功
2. `PARTIAL_SUCCESS`：部分成功
3. `FAILED`：同步失败

### 6.3 分类状态

1. 已匹配分类
2. 待分类

## 7. 接口设计建议

### 7.1 后台接口

1. `POST /admin/book/sync`：手动触发同步。
2. `GET /admin/book/sync/log/list`：查询同步日志。
3. `POST /admin/book/{id}/online`：上架书籍。
4. `POST /admin/book/{id}/offline`：下架书籍。

### 7.2 前台接口

1. `GET /book/list`：分页查询书籍。
2. `GET /book/{id}`：查看书籍详情。
3. `GET /book/read/{id}`：获取阅读入口。

## 8. 代码骨架建议

建议按业务模块优先组织目录，而不是只按技术层平铺，以便后续维护：

1. `controller/book`
2. `controller/admin`
3. `service/book`
4. `service/category`
5. `service/read`
6. `service/sync`
7. `domain/book`
8. `domain/category`
9. `domain/read`
10. `mapper`
11. `entity`
12. `dto`
13. `vo`
14. `config`
15. `schedule`
16. `client/external`

## 9. 风险与注意事项

1. 外部 API 字段结构可能存在差异，需通过转换器隔离影响。
2. 同一本书可能存在多种格式链接，阅读模块需优先级判定。
3. 分类规则较弱，V1 可用，但后续需留出扩展点。
4. 书籍上架状态必须与前台查询隔离，否则易出现未审核数据暴露。

## 10. 后续实现建议

1. 先生成实体、Mapper、Service、Controller 的基础骨架。
2. 再补充同步日志表与定时任务能力。
3. 前端采用 Vue2 + Element UI 单独子项目 `bookhub-web/`，通过 Axios 对接后端接口。
4. 前端路由包含首页、详情页、阅读页和后台管理页，开发阶段通过代理转发到 `http://localhost:8080`。
5. 最后补测试用例和初始化 SQL。

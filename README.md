---
项目: BookHub-在线电子书阅读平台
作者: houliangyu
日期: 2026-04-30
编号: SPI-PROJ-BHub-README
阶段: 项目级说明
状态: 草案
---

# BookHub 项目说明

## 1. 项目简介

BookHub 是一个基于公开电子书数据源构建的在线电子书阅读平台，当前仓库采用前后端分离、同仓库管理模式：

1. 后端：`Spring Boot 2.7.x + JDK 8 + MyBatis-Plus + MySQL 8.0`
2. 前端：`Vue2 + Vue Router + Axios + Element UI`

## 2. 当前目录结构

```text
codex-clound
├── bookhub-web/                           # Vue2 前端子项目
├── src/main/java/com/example/demo/        # Spring Boot 后端代码
├── src/main/resources/sql/                # MySQL 8.0 初始化脚本
├── src/test/                              # 后端测试
├── .docs/过程管理文档/                     # CMMI 过程文档
├── 项目产品文档.md                         # 产品定义与范围说明
├── 开发规范文档.md                         # 项目开发与协作规范
├── 业务进度文档.md                         # 当前业务进展与下一步计划
└── README.md                              # 项目总入口
```

## 3. 项目级文档入口

1. [项目产品文档.md](/Users/houliangyu/myproject/codex-clound/项目产品文档.md)
2. [开发规范文档.md](/Users/houliangyu/myproject/codex-clound/开发规范文档.md)
3. [业务进度文档.md](/Users/houliangyu/myproject/codex-clound/业务进度文档.md)
4. [业务流程梳理任务文档](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-01-业务流程梳理/README.md)
5. [项目文档整理任务文档](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-02-项目文档整理/README.md)

## 4. 当前可用能力

### 4.1 前端

1. 首页：搜索、分类筛选、卡片列表、分页。
2. 详情页：封面、作者、简介、分类、开始阅读。
3. 阅读页：支持 `html` 和 `txt`。
4. 后台页：书籍列表、手动同步、上下架、同步日志。

### 4.2 后端

1. 书籍列表：`GET /book/list`
2. 书籍详情：`GET /book/{id}`
3. 阅读信息：`GET /book/read/{id}`
4. 分类列表：`GET /book/category/list`
5. 手动同步：`POST /admin/book/sync`
6. 上下架：`PUT /admin/book/status`
7. 同步日志：`GET /admin/book/sync/log/list`

## 5. 启动方式

### 5.1 后端

```bash
mvn -Dmaven.repo.local=/tmp/codex-m2 spring-boot:run
```

默认端口：`8080`

### 5.2 前端

```bash
cd bookhub-web
npm run serve
```

默认端口：`3000`

## 6. 文档维护约定

1. 产品范围变化时，优先更新 `项目产品文档.md`。
2. 技术约束、目录规范、联调方式变化时，优先更新 `开发规范文档.md`。
3. 功能完成状态、下一步计划、风险变化时，优先更新 `业务进度文档.md`。
4. 涉及较大任务时，同步更新 `.docs/过程管理文档/` 下对应 TaskID 的需求、设计、实现和评审文档。

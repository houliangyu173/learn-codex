---
项目: BookHub-在线电子书阅读平台
作者: houliangyu
日期: 2026-04-30
编号: SPI-PROC-CoreBiz-README
阶段: 02-02需求/02-03设计/02-04实现
状态: 已基线
---

# 任务总览

## 1. TaskID

`bookhub-20260430-03-核心业务增强`

## 2. 任务背景

当前 BookHub 已完成前后端基础联调，但核心业务仍存在三项关键短板：

1. 同步流程仍为样例数据写入，未真正对接公开电子书 API。
2. 用户书架能力尚未接入前后端。
3. 后台同步日志能力仅支持基础展示，缺少增强查询与信息完整性。

## 3. 本轮优先顺序

1. 真实采集流程
2. 书架功能
3. 后台日志增强

## 4. 本轮目标

1. 将书籍同步切换为真实外部 API 采集，并保留失败兜底与日志记录能力。
2. 提供 V1 可用的书架列表、加入书架、进度更新能力。
3. 增强后台同步日志查询条件与展示内容。

## 5. 当前完成情况

1. 已完成 Gutendex 真实采集接入，并补充 Open Library 作为字段补全与数量兜底来源。
2. 已完成书架接口、书架页面与详情页/阅读页联动。
3. 已完成后台同步条件输入、日志筛选与日志字段增强。

## 6. 文档清单

1. [需求规格说明书(SPI-REQ-CoreBiz-SRS)V1.0.md](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-03-核心业务增强/02-02需求/需求规格说明书(SPI-REQ-CoreBiz-SRS)V1.0.md)
2. [概要设计说明书(SPI-DES-CoreBiz-GD)V1.0.md](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-03-核心业务增强/02-03设计/概要设计说明书(SPI-DES-CoreBiz-GD)V1.0.md)
3. [实现过程文件(SPI-DEV-CoreBiz-ProcDoc)V1.0.md](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-03-核心业务增强/02-04实现/实现过程文件(SPI-DEV-CoreBiz-ProcDoc)V1.0.md)
4. [评审报告(SPI-PPQA-CoreBiz-ReviewReport)V1.0.md](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-03-核心业务增强/02-04实现/评审报告(SPI-PPQA-CoreBiz-ReviewReport)V1.0.md)
5. [文档索引表(SPI-CM-CoreBiz-Index)V1.0.md](/Users/houliangyu/myproject/codex-clound/.docs/过程管理文档/bookhub-20260430-03-核心业务增强/文档索引表(SPI-CM-CoreBiz-Index)V1.0.md)

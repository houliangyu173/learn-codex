# Spring Boot JDK8 Web 脚手架项目信息

## 1. 项目定位
这是一个**基于 JDK 8 + Spring Boot 2.7.x** 的最小可运行 Web 脚手架项目，适合作为业务系统起步模板。

## 2. 技术栈
- JDK 8
- Spring Boot 2.7.18
- Spring MVC（`spring-boot-starter-web`）
- Maven
- JUnit 5（测试）

## 3. 项目结构
```text
springboot-jdk8-scaffold
├── pom.xml
├── PROJECT_INFO.md
└── src
    ├── main
    │   ├── java
    │   │   └── com/example/demo
    │   │       ├── ScaffoldApplication.java          # 启动类
    │   │       ├── common/                           # 通用常量、工具类（预留）
    │   │       ├── config/                           # 配置类（预留）
    │   │       ├── controller/
    │   │       │   └── HealthController.java         # 示例接口
    │   │       └── service/
    │   │           └── impl/                         # 服务实现（预留）
    │   └── resources
    │       └── application.yml
    └── test
        └── java/com/example/demo
            └── ScaffoldApplicationTests.java
```

## 4. 示例接口
- URL: `GET /api/health`
- 作用: 返回服务健康状态与当前时间

示例返回：
```json
{
  "status": "UP",
  "service": "springboot-jdk8-scaffold",
  "timestamp": "2026-04-28T12:00:00"
}
```

## 5. 快速启动
```bash
mvn clean package
mvn spring-boot:run
```

启动后访问：
- http://localhost:8080/api/health

## 6. 后续扩展建议
1. 引入统一返回体和全局异常处理
2. 接入日志规范（Logback + traceId）
3. 集成数据库访问层（MyBatis / JPA）
4. 增加多环境配置（dev/test/prod）
5. 补充接口文档（SpringDoc OpenAPI）

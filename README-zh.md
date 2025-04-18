# Easy SpringBoot Template

[English](./README.md) | 简体中文

> 一个轻量级、易于使用的 Spring Boot 启动模板，基于 **Spring Boot 2.7.0** 版本。

---

## 适用人群

-   觉得 Spring Boot 过于复杂的开发者
-   想尽快启动应用程序的开发者

---

## 应用钩子

-   **所有 Bean 初始化之前**: 在任何 Spring Bean 初始化之前，执行自定义逻辑。
    适合进行一些需要在数据库相关 Bean 启动前完成的高优先级任务。

-   **所有 Bean 初始化之后**: 在所有 Spring Bean 初始化完成后，执行自定义逻辑。
    适合依赖于数据库操作的功能。

## MVC 钩子

-   **处理请求之前/后**: 在进入 Controller 方法前，执行自定义逻辑。

-   **异常处理**: 集中管理异常处理逻辑。

-   **响应体定制**: 全局性地自定义响应体，在返回给客户端前进行处理。

## SQL 表结构初始化

-   **动态表名**: 根据运行时信息动态编造表名。

-   **条件式创建索引**: 根据条件，选择性创建数据库索引。

---

## 快速上手

-   克隆项目:

    ```bash
    mkdir -p ~/projects
    cd ~/projects
    git clone git@github.com:ArcaneEcholan/easy-spring-template.git
    cd easy-spring-template
    ```

-   启动一个数据库:

    直接 docker compose 启动

    ```sh
    sudo docker compose -f ./docker-compose-db.yml up -d
    ```

    配到配置文件里: `src/main/resources/application.yml`:

    ```
    # ============== custom ==============
    db:
        user: root
        password: root
        name: test
        host: localhost
        port: 33061
    ```

-   运行 `App.java` 即可

---

## 协议

本项目依据 MIT License 协议发布。

---

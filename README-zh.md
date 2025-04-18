# Easy SpringBoot Template

[English](./README.md) | 简体中文

> 一个轻量级、易于使用的 Spring Boot 启动模板，基于 **Spring Boot 2.7.0** 版本。

---

## 适用人群

- 觉得 Spring Boot **过于复杂**的开发者
- 想尽快启动一个 Spring Boot 应用程序

---

## 应用钩子

- `BeforeBeanInitHook`: Beans 创建之前，执行高优先级逻辑。
- `AfterBeanInitHook`: Beans 初始化后，执行自定义逻辑。

## MVC 钩子

Controller 逻辑异常：
beforeMvcRequest -> exceptionHappened -> beforeWritingBody -> afterMvcRequest

Spring 框架逻辑异常（比如没找到路由）：
beforeMvcRequest -> **NONE** -> **NONE** -> afterMvcRequest -> Handle Extra Exception

- `beforeMvcRequest/afterMvcRequest`: 在进入 Controller 方法前后，执行自定义逻辑。

- `exceptionHappened`: 集中管理异常处理逻辑。

- `beforeWritingBody`: 全局性地自定义响应体，在返回给客户端前进行处理。

- `Handle Extra Exception`: 处理一些业务代码之外的异常，比如 Spring 框架找不到路由这种无法定制的异常，或者没有被 exceptionHappened 处理的异常，都会进入这个特殊异常处理逻辑。（最常见是404页面的定制）。

## SQL 表结构初始化

- **动态表名**: 根据运行时信息动态编造表名。

- **条件式创建索引**: 根据条件，选择性创建数据库索引。

---

## 快速上手

- 克隆项目:

  ```bash
  mkdir -p ~/projects
  cd ~/projects
  git clone git@github.com:ArcaneEcholan/easy-spring-template.git
  cd easy-spring-template
  ```

- 运行 `App.java` 即可（无需数据库）

## 连接数据库

* 配置: `db.enabled` 调成 `true`

* 一键启动 MySQL:

    ```sh
    sudo docker compose -f ./docker-compose-db.yml up -d
    ```

* 配置: `src/main/resources/application.yml`:

    ```
    # ============== custom ==============
    db:
        enabled: true
        user: root
        password: root
        name: test
        host: localhost
        port: 33061
    ```

---

## 协议

本项目依据 MIT License 协议发布。

---

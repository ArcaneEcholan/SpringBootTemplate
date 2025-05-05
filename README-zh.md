# Easy SpringBoot Template

[English](./README.md) | 简体中文

> 一个轻量级、易于使用的 Spring Boot 启动模板，基于 **Spring Boot 2.7.0** 版本。

---

## 适用人群

-   觉得 Spring Boot **过于复杂**的开发者
-   想尽快启动一个 Spring Boot 应用程序

---

## 应用钩子

在 Sprign IOC Bean 创建前后进行定制操作，例如决定是否初始化数据库连接。

| Hook Name              | Desc                                        |
| ---------------------- | ------------------------------------------- |
| **BeforeBeanInitHook** | Spring IOC Beans 创建之前，执行高优先级逻辑 |
| **AfterBeanInitHook**  | Spring IOC Beans 初始化后，执行自定义逻辑   |

## MVC 钩子

`beforeMvcRequest` → `handleEx/handleExtraEx` → `beforeWritingBody` → `afterMvcRequest`

| 钩子名称                   | 描述             |
| -------------------------- | ---------------- |
| **beforeMvcRequest**       | 请求前           |
| **handleEx/handleExtraEx** | 处理请求逻辑异常 |
| **beforeWritingBody**      | **返回请求体**前 |
| **afterMvcRequest**        | 请求后           |

## SQL 表结构初始化

-   **动态表名**: 根据运行时信息动态编造表名。

-   **条件式创建索引**: 根据条件，选择性创建数据库索引。

## 作为静态服务器

**概览**

支持 `vue2js`, `next.js`.

命令：

```shell
java -jar xx.jar --serve_static="/path/to/static/"
```

-   serve_static 参数指定磁盘上的 static 文件夹路径。
    | value | |
    | ------------------ | ---- |
    | "/path/to/static/" | good |
    | "/path/to/static" | bad: 没有以 "/" 结尾 |
    | "path/to/static" | bad: 没有以 "/" 开头 |


**使用 docker compose 启动**

```
staticserver/
├── server.jar
├── docker-compose.yml
├── .env(optional)
└── static/
    └── index.html
```

docker-compose.yml

```yml
version: "3.8"

services:
    staticserver:
        image: eclipse-temurin:8-alpine
        container_name: "${CONTAINER_NAME:-staticserver}"
        restart: always
        ports:
            - "${LOCAL_PORT:-8080}:8080"
        volumes:
            - "${SERVER_JAR:-./server.jar}:/server.jar"
            - "${STATIC_LOCATION:-./static}:/static"
        command: >
            java -Dfile.encoding=UTF-8 -jar /server.jar
            --serve_mode="nextjs"
            --serve_static="/static/"
```

.env
```
CONTAINER=staticserver
LOCAL_PORT=8080
STATIC_LOCATION=custom/static
SERVER_JAR=custom/server.jar
```

start static server

```shell
docker compose \
-f ${staticserver}/docker-compose.yml \
up -d
```

stop static server

```shell
docker compose \
-f ${staticserver}/docker-compose.yml \
down
```

---

## 快速上手

-   克隆项目:

    ```bash
    mkdir -p ~/projects
    cd ~/projects
    git clone git@github.com:ArcaneEcholan/easy-spring-template.git
    cd easy-spring-template
    ```

-   运行 `App.java` 即可（无需数据库）

## 连接数据库

-   配置: `db.enabled` 调成 `true`

-   一键启动 MySQL:

    ```sh
    sudo docker compose -f ./docker-compose-db.yml up -d
    ```

-   配置: `src/main/resources/application.yml`:

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

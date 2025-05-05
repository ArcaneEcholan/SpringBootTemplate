# Easy SpringBoot Template

English | [简体中文](./README-zh.md)

> A lightweight and easy-to-use Spring Boot startup template, based on **Spring
> Boot 2.7.0**.

---

## Who Should Use It

-   developers who find Spring Boot too complex
-   developers who want a fast application startup

---

## App Hooks

Useful for customization before and after IOC bean creation, eg. decide whether
to init db connection.

| Hook Name              | Desc                                  |
| ---------------------- | ------------------------------------- |
| **BeforeBeanInitHook** | before spring ioc bean initialization |
| **AfterBeanInitHook**  | after spring ioc bean initialization  |

## MVC Hooks

`beforeMvcRequest` → `handleEx/handleExtraEx` → `beforeWritingBody` → `afterMvcRequest`

| Hook Name                  | Desc                             |
| -------------------------- | -------------------------------- |
| **beforeMvcRequest**       | before mvc request               |
| **handleEx/handleExtraEx** | handle mvc **exceptions**        |
| **beforeWritingBody**      | before writing **response body** |
| **afterMvcRequest**        | after mvc request                |

## SQL Schema Init

-   **Dynamic Table Name**: Dynamically build table names based on runtime
    context.

-   **Conditional Index Creation**: Conditionally create database indexes when
    needed.

## Host Static Pages

**Basic Usage**

Support serving `vue2js`, `next.js`.

```shell
java -jar xx.jar --serve_static="/path/to/static/"
```

-   serve_static: specify a static folder on the filesystem to serve.
    | value | |
    | ------------------ | ---- |
    | "/path/to/static/" | good |
    | "/path/to/static" | bad: not end with "/" |
    | "path/to/static" | bad: not start with "/" |

**Start using docker compose**

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

## Quick Start

-   clone the project:

    ```bash
    mkdir -p ~/projects
    cd ~/projects
    git clone git@github.com:ArcaneEcholan/easy-spring-template.git
    cd easy-spring-template
    ```

-   use **jdk8** to compile

-   run `App.java`

---

## Run with DB (Optional)

-   Set `db.enabled: true`.

-   Start Database with docker:

    ```sh
    sudo docker compose -f ./docker-compose-db.yml up -d
    ```

-   Configure db properties at: `src/main/resources/application.yml`:

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

## License

This project is licensed under the MIT License.

---

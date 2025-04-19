# Easy SpringBoot Template

English | [简体中文](./README-zh.md)

> A lightweight and easy-to-use Spring Boot startup template, based on **Spring Boot 2.7.0**.

---

## Who Should Use It

-   developers who find Spring Boot too complex
-   developers who want a fast application startup

---

## App Hooks

-   **Before All Beans Init**: Run custom logic **before** any Spring bean is initialized.
    useful for high-priority tasks that must happen before even database beans are ready.

-   **After All Beans Inited**: Run custom logic **after** all Spring beans are initialized.
    helpful for features that depend on database access.

## MVC Hooks

Controller Logic Exception:
`beforeMvcRequest` → `exceptionHappened/Handle Extra Exception` → `beforeWritingBody` → `afterMvcRequest`

- **beforeMvcRequest / afterMvcRequest**
  custom logic executed before and after entering the controller method.

- **exceptionHappened**
  centralized logic for handling controller exceptions.

- **Handle Extra Exception**
  handles exceptions outside business logic, such as those from the spring framework (e.g., route not found) that can’t be customized through regular exception handling.
  this includes exceptions not handled by `exceptionHappened` — most commonly used for custom 404 pages.

- **beforeWritingBody**
  global customization of the response body, processed right before it’s returned to the client.

## SQL Schema Init

-   **Dynamic Table Name**: Dynamically build table names based on runtime context.

-   **Conditional Index Creation**: Conditionally create database indexes when needed.

---

## Quick Start

-   clone the project:

    ```bash
    mkdir -p ~/projects
    cd ~/projects
    git clone git@github.com:ArcaneEcholan/easy-spring-template.git
    cd easy-spring-template
    ```

-   run `App.java` without configuring a db by default

---

## Run with DB

* Set `db.enabled: true`.

* Start Database with docker:

    ```sh
    sudo docker compose -f ./docker-compose-db.yml up -d
    ```

* Configure db properties at: `src/main/resources/application.yml`:

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

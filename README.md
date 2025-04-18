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

-   **Before/After Handle Request**: Hook custom logic before entering a controller method.

-   **Exception Handlers**: Centralized exception management.

-   **Response Body Customization**: Globally customize response bodies before returning to clients.

## SQL Schema Init

-   **Dynamic Table Name**: Dynamically build table names based on runtime context.

-   **Conditional Index Creation**: Conditionally create database indexes when needed.

---

## Quick Start

-   clone the project:

    ```bash
    git clone git@github.com:ArcaneEcholan/easy-spring-template.git
    ```

-   import into your IDE (IntelliJ IDEA recommended)

-   run `App.java` and access your endpoints

---

## License

This project is licensed under the MIT License.

---

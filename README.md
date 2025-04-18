# Project Overview

English | [简体中文](./README-zh.md)

## Introduction

> A template based on **Spring Boot 2.7.0**, providing a set of features to simplify app startup.

---

## Features

### App Start Hooks

- **Before All Beans Init**
    - Execute custom logic **before** any Spring beans are initialized. Typical customization could be that there are some high-priority task that need to be done even before database beans has been initialized.

- **After All Beans Inited**
    - Execute custom logic **after** all Spring beans have been initialized. It's very useful for functions relying on databases.

### SQL Schema Init

- **Dynamic Table Name**
    - Support dynamic construction of table names based on runtime context.

- **Conditionally Create Table Index**
    - Allow conditional creation of database indexes.

### MVC

- **Request Context**
    - Provide a lightweight request context holder to simplify access to HTTP request/response/session.

- **Hooks**

    - **Before Handle Request**
        - Hook custom logic before entering controller handler.

    - **Exception Handlers**
        - Centralized exception handling for uniform API error responses.

    - **Response Body Customization**
        - Customize or wrap controller responses globally.

    - **Static Resource Error Customization**
        - Customize error responses when accessing static resources.

---

## Quick Start

1. clone the project
   ```bash
   git clone https://github.com/your/repo.git
   ```

2. import into your favorite IDE (IntelliJ IDEA recommended)

3. run `Application.java` and access your endpoints

---

## License

This project is licensed under the MIT License.


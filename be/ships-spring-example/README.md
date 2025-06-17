# Ships Map – Backend (Java 21 + MSSQL)

This is the backend for the **Ships Map** project, built with Spring Boot and MSSQL.

## 🔍 Overview

[todo: add text here]

---

## 🛠 Tech Stack

- **Java 21**
- **Spring Boot**
- **Maven**
- **MSSQL**
- **Flyway** (for the db migration)
- **Docker** (to host the db)
- **Kafka Producer - Python**
- **Kafka Consumer - Java**

---

## 📁 Project Structure

```
be/ships-spring-example/
├── .idea/                                 # IntelliJ project files
├── .mvn/                                  # Maven wrapper
│   └── wrapper/
├── config/
│   └── checkstyle/                        # Code style configuration
├── src/
│   └── main/
│       ├── java/
│       │   └── gr.uoa.di.ships/
│       │       ├── api/
│       │       │   ├── dto/               # Data Transfer Objects
│       │       │   └── mapper/            # DTO/entity mappers
│       │       ├── configurations/
│       │       │   ├── cors/              # CORS setup
│       │       │   ├── exceptions/        # Exception handling
│       │       │   ├── kafka/             # Kafka consumer config
│       │       │   ├── migrations/        # Initialize the db with migrations if needed
│       │       │   ├── schedulers/        # Schedulers config
│       │       │   └── security/          # Security config
│       │       │   └── websockets/        # WebSocket config
│       │       ├── controllers/           # REST controllers
│       │       ├── persistence/
│       │       │   ├── model/             # JPA entities and Enums
│       │       │   └── repository/        # Spring Data repositories
│       │       ├── services/
│       │       │   ├── implementation/    # Business logic
│       │       │   └── interfaces/        # Service interfaces
│       │       └── ShipsApplication.java  # Main Spring Boot entry point
│       └── resources/
│           ├── assets/                    # CSV files used in the app for migration
│           ├── db.migration/              # Flyway SQL migrations
|           ├── ssl/                       # Local SSL setup
│               ├── ships.crt
│               ├── ships.key
│               └── ships.p12
│           └── application.properties     # Spring Boot config file
├── test/
│   └── java/
│       └── gr.uoa.di.ships.services/      # Unit tests for services
├── pom.xml                                # Maven build configuration
└── README.md                              # Project overview and setup instructions

```

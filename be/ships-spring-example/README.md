# seeSea – Backend (Spring Boot Java + MSSQL)

This is the backend for the **seeSea** project, built with Spring Boot, Java, MSSQL and many more!

---

## 🔍 Overview

**SeeSea** is a role-based ship tracking web application with a backend system designed to support distinct user experiences for **Admins**, **Registered Users**, and **Guests**. The backend handles authentication, user management, fleet operations, and geofencing alerts while enforcing access control across all services.

### 🔐 Authentication & Authorization
- Supports user **sign-up**, **sign-in**, and **secure session management**.
- Implements **role-based access control** to distinguish between admin, registered, and guest users.
- Protects premium features (e.g., alerts, filters) from unauthorized access.

### 👥 User Management
- Manages user profiles and settings.
- Supports persistent storage of user-specific data like **personal fleets** and **alert configurations**.

### 🚢 Ship Data Management
- Provides APIs to **retrieve**, **display**, and **update** vessel data.
- Admin users can edit static ship attributes (e.g., vessel type).
- All users can view live ship data and access individual ship details.

### ⚓ Fleet Operations
- Enables users to **add** or **remove** vessels from their personal fleet.
- Maintains a persistent relationship between users and their selected vessels.

### 🧭 Filters & Alerts
- Allows users to apply **filters** based on ship type and operational status.
- Supports **zone-based alerts** using geofencing logic.

### ⚙️ System Responsibilities
- Integrates with real-time vessel data sources via a kafka consumer.
- Ensures **data consistency**, **security**, and **high availability** of all core services.

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

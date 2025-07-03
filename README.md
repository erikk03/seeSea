<img src="https://github.com/erikk03/seeSea/blob/main/fe/ships-map/public/logo.png?raw=true"
     alt="SeeSea logo" width="250" />

# Α VTS (Vessel Traffic Services) App

## 👨‍💻 Team Members – Team 1

| AM           | Name                                                    | Email                        |
|--------------|---------------------------------------------------------|------------------------------|
| 11152100043  | [Kajacka Erik](https://github.com/erikk03)              | sdi2100043@di.uoa.gr         |
| 11152100045  | [Kalampokis Evaggelos](https://github.com/vaggeliskg)   | sdi2100045@di.uoa.gr         |
| 11152100108  | [Moumoulidis Anastasios](https://github.com/sdi2100108) | sdi2100108@di.uoa.gr         |
| 11152100192  | [Tselikas Panagiotis](https://github.com/sdi2100192)    | sdi2100192@di.uoa.gr         |
| 11152100275  | [Chrysos Dimitrios](https://github.com/DimitrisChrysos) | sdi2100275@di.uoa.gr         |

## 📝 Project Overview

This repository contains the implementation of **"seeSea"**, a Vessel Traffic Services (VTS) app, with real-time vessel tracking and maritime monitoring developed for the course **Software Technology [YΣ09] – Spring Semester 2025**.

The platform leverages **AIS (Automatic Identification System)** data to support real-time vessel location tracking, zone violation alerts, and fleet management functionality. This system aims to improve maritime situational awareness and supports secure, and scalable monitoring for various user roles.

## 🎯 Assignment Objectives

As defined in the assignment, the goal was to develop a complete web platform with the following phases:

### Part 1: Requirements & Design
- `./docs/srs.md`: Software Requirements Specification (SRS) including functional and non-functional requirements.
- `./docs/ui.md`: UI wireframes and user interface prototypes (designed with Figma).

### Part 2: System Architecture & Presentation
- `./docs/presentation.pdf`: Presentation with UML diagrams, detailed implementation plans, and updated design decisions based on Part 1.

### Part 3: Implementation
- `./src/`: Full-stack implementation of the system using the technologies described below.

---

## ⚒️ Technologies Used

- **Frontend:** React.js with HeroUI components and Leaflet.js for interactive mapping
- **Backend:** Spring Boot (Java) exposing RESTful APIs, integrated with WebSocket for real-time updates
- **Streaming:** Apache Kafka with a Python AIS data producer and Java consumer
- **Database:** MSSQL, containerized via Docker and managed with Flyway for schema versioning
- **Security:** HTTPS (self-signed cert), JWT-based authentication, role-based access
- **Version Control:** Git (hosted on GitHub)

---

## 🧩 Key Features

- **📡 Live Vessel Map:** Real-time AIS data stream replayed through Kafka for live ship tracking
- **🛟 Zone of Interest (ZOI):**
  - User-defined circular zones
  - Speed thresholds and violation alerts
  - Single active ZOI per user
- **📜 Vessel History:** Past 12 hours of vessel movements
- **📁 Fleet Management:** Users manage personal fleets, viewable on map
- **🔍 Filtering & Search:** By vessel type/status, fleet, MMSI
- **🔔 Notification System:** Real-time, persistent user alerts
- **🔐 Role-Based Access:**
  - Admins can edit static vessel attributes (e.g., vessel type)
  - Registered and admin users have access to more features in the app

---

## 🔐 Security & Compliance

- HTTPS secured communication
- GDPR-compliant user data handling
- Role-based permission management

---

## 📂 Project Structure

```
SOFTWARETECHNOLOGY/
├─ be/ships-spring-example/        # Backend Spring Boot application
├─ docs/
│  ├─ assets/
│  ├─ Presentation.pdf             # Project presentation slides
│  ├─ srs.md                       # Software Requirements Specification
│  ├─ ui.md                        # UI mockups and design
│  └─ UML-ClassDiagram.pdf         # UML Class Diagram
├─ fe/ships-map/                   # Frontend (React)
├─ kafka/VTS/
│  ├─ dataset/                     # AIS dataset files
│  └─ kafka_2.12-3.9.0/            # Apache Kafka distribution
├─ .gitignore
├─ README.md                       # Project overview and documentation
```

## ⚠️ SetUp and Configuration before Running

In order to run properly user must have:

1. **Kafka 3.9.0 and Zookeeper must be installed**
2. **Docker must be installed (Docker Desktop is also useful)**
3. **Node.js must be installed**
4. **Java 21 must be installed**
5. **Python, Pandas, and Confluent Kafka must be installed**
6. **Database must be configured as described below:**
    - Pull Image:
      ```bash
      docker pull mcr.microsoft.com/mssql/server:2019-latest
      ```
    
    - Run docker:
      ```bash
      docker run -e "ACCEPT_EULA=Y" -e "MSSQL_SA_PASSWORD=Password()2003" -p 1433:1433  --name seesea --hostname sqlserver -d mcr.microsoft.com/mssql/server:2019-latest
      ```
    - Connect project data source (IntelliJ recommended):
      - Host: localhost
      - Port: 1433
      - User: sa
      - Password: Password()2003
      - Driver: MSSQL Server 

    - Run query to create database (IntelliJ Query Console recommended):
      ```bash
      CREATE DATABASE SeeSea;
      ```
7. **Folder dataset must be downloaded independently**
    - Download Kafka_intro.zip folder from:
      https://owncloud.skel.iit.demokritos.gr/index.php/s/9EsxBK0Bk4ebudk
    - Export folder
    - Move dataset folder from Kafka_intro/VTS/ to /kafka/VTS/


## 🚀 How to Run

Follow these steps to run the full system locally:

1. **Start the Docker Image using Docker**

2. **Start the Backend**
   - Navigate to the backend directory:
     ```bash
     cd be/ships-spring-example
     ```
   - Build and run the Spring Boot application

3. **Start Kafka**
   - Go to the Kafka folder:
     ```bash
     cd kafka/VTS/kafka_2.12-3.9.0
     ```
   - Start Zookeeper and Kafka servers:
     ```bash
     ./start.sh
     ```

4. **Run the Dataset Producer**
   - Execute the Python script that streams AIS data to Kafka:
      ```bash
      cd kafka/VTS/dataset
      python3 producer.py
      ```

5. **Start the Frontend**
   - Navigate to the frontend directory:
     ```bash
     cd fe/ships-map
     ```
   - Install dependencies and start the development server:
     ```bash
     npm install
     ./start.sh
     ```

## 🤖 Admin Credentials
To log in as an admin, you can use the following:
- email: admin@seesea.com
- password: admin

## 💥 Collision Detection (Bonus task)
Checks for collisions between 2 or more vessels and returns an alert.
- Motionless vessels are skipped from the calculations (cannot collide with others, but others can collide with them)
- If vessels have a distance greater than 10.000 meters collision check will not be calculated.
- A distance greater than 1.000 meters is considered safe (no alerts will be sent, but collision checks will be made).
- Collision detection is based on a 15-minute predictive horizon. (If a CPA distance smaller than 1.000 meters is to be reached in the next 15 minutes, an alert will be sent)

CPA Reference:
[On the closest point of approach (CPA)](https://pierdusud.com/en/on-the-closest-point-of-approach)




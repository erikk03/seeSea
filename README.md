# **[YΣ09] SOFTWARE TECHNOLOGY 2025**

## **TEAM MEMBERS [TEAM 1]**

| AM          | NAME                   | EMAIL                |
| ----------- | ---------------------- | -------------------- |
| 11152100043 | KAJACKA ERIK           | sdi2100043@di.uoa.gr |
| 11152100045 | KALAMPOKIS EVAGGELOS   | sdi2100045@di.uoa.gr |
| 11152100108 | MOUMOULIDIS ANASTASIOS | sdi2100108@di.uoa.gr |
| 11152100192 | TSELIKAS PANAGIOTIS    | sdi2100192@di.uoa.gr |
| 11152100275 | CHRYSOS DIMITRIOS      | sdi2100275@di.uoa.gr |

## **ASSIGMENT**

### **PART 1**

./docs/srs.md: SRS– Software Requirements Specification
./docs/ui.md: UI design using figma

### **PART 2**

./docs/presentation.pdf:
Slides presentation describing how we will implement the requirements in the SRS and possible changes from previous version.
Presentation will have an UML diagram about specific part of the system.
Also some UI wireframes from the part 1 of the assigment.

### **PART 3**

PART 3  
./src/: Implementation of the system

In this final part, we implemented a real-time vessel monitoring platform based on the specifications defined in Parts 1 and 2. The platform allows users to visualize live ship movements, receive alerts, and manage their Zones of Interest (ZOIs). The system is designed with scalability, real-time performance, and role-based access in mind.

#### 🧱 Core Technologies
- **Frontend**: React.js with HeroUI for UI components and Leaflet for interactive maps.
- **Backend**: Spring Boot with RESTful APIs and WebSocket support for live data.
- **Streaming**: Apache Kafka with a Python producer (AIS data) and a Java consumer.
- **Database**: MSSQL with Docker and Flyway for schema migration.
- **Security**: JWT-based authentication, HTTPS with self-signed certificates.
- **Version Control**: Git + GitHub.

#### ⚙️ Key Features Implemented
- **Live Map**: Real-time display of vessel positions via Kafka and WebSocket.
- **Zone of Interest**:
  - Users can draw a circular zone.
  - Define speed threshold, and enable alerts for zone entry/exit.
  - Violations trigger real-time notifications.
- **Vessel History**:
  - View past movements of a vessel for the last 12 hours.
- **Fleet Management**:
  - Registered users can save and manage their personal fleet.
- **Filtering and Search**:
  - Filter vessels by type, status, or fleet membership.
  - Live search by MMSI with map zoom and highlight.
- **Notification System**:
  - Persistent alert notifications per user with delete capability.
- **Role-Based Access**:
  - Administrators can edit static vessel data (e.g. vessel type).

#### 🔒 Security & Compliance
- Encrypted data transmission using HTTPS.
- Role-based access control.
- GDPR-compliant handling of user data.

This implementation completes the system as envisioned in the SRS and aligns with the design decisions from the presentation.

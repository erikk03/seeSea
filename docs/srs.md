# **SRS – Software Requirements Specification [Only Requirements Analysis]**

## System Purpose

The proposed system is an online platform for real-time monitoring of maritime activity based on AIS data. Using the **Automatic Identification System (AIS)** technology, which involves ship tracking via data exchange through VHF transceivers, GPS, and other maritime sensors (such as gyroscopes), the system collects valuable real-time data about the ships. AIS data is acquired through AIS base stations along the coasts or via satellites when the ship is beyond the reach of terrestrial networks.

The platform will provide real-time ship tracking on a map, showing ships based on their position, status, type, and course. It will also allow the visualization of a ship’s past route (up to the last 12 hours). Registered users will be able to save ships of interest, apply filters to display ships (e.g., by fleet or ship type), and define zones of interest (areas on the map with set restrictions). When these restrictions are violated, users will receive alerts.

## User Roles

- **Anonymous User** : Access only to the map view with ship locations and information.
- **Registered User** : Additional features (saving ships, applying filters and defining zones of interest).
- **Administrator** : Rights to edit static ship data.

## Constraints

- The system will be based on **React.js** for the front end and will be an **SPA (Signle Page Application)**.
- The backend sub-system will be based on **Spring Boot** and support **RESTful API**.
- It will use **Apache Kafka** for AIS data streaming.
- All data will be exchanged via **HTTPS** with **self-signed certificate**.
- The source code will be stored in a **private** [Git repository](https://github.com/erikk03/softwareTechnology).
- A build automation tool will be used.
- A software test automation tool will be used for the backend sub-system.

## Assumptions

- AIS base stations and satellite services provide reliable data.
- Only one zone of interest can be applied at a time.
- Primary platform language is going to be english.
- Admins have also the ability to use the registered users features.

## Requirements

### Functional Requirements

- Real time visualization of ships on the map based on their location, status, type and course.
- User authentication and authorization.
- Tracking the previous routes of ships (for the past 12 hours).
- Allowing registered users to save ships of interest to their fleet.
- Allowing registered users to display ships on the map based on filters (e.g. type, my fleet).
- Registered users can create zones of interest with restrictions for which a notification will be sent when violated.
- Registered users will have access to a notification list.
- Administrators can edit static data about the ships (e.g. their type).
- Users can search for specific vessels using a search bar.

### Non Functional Requirements

- The system must ensure high availability to provide continous monitoring of maritime activity.
- User authentication and data transmissions must be secured using **HTTPS**.
- **Data processing** and transformation to the **required format**.
- **Data transfer** across our app (kafka, backend, frontend) will use the **JSON** format.
- System must provide **role-based access control** to ensure that only authorized users have access into sensitive functionalities.
- The platform must handle an increasing number of users and AIS data streams efficiently.
- The system shall utilize **Apache Kafka** as the messaging infrastructure for AIS data streaming to ensure reliable, scalable, and real-time data processing.
- **Response time** must be low so it updates AIS data frequently enough.
- **GDPR** compliance must be adhered regarding user data storage and handling.
- The codebase must follow best practices and be maintainable with proper version control using **Git**.
- **Automated testing** should be incorporated to ensure maintability and sustainability.

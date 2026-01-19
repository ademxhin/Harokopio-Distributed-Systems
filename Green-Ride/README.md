# GreenRide 🚗🌱
Distributed Systems Project – Harokopio University

## Repository
The complete source code, including commit history and documentation, is available on GitHub:

https://github.com/ademxhin/Harokopio-Distributed-Systems

## Description
**GreenRide** is a web-based carpooling application designed to facilitate student transportation to and from the University or the city center.  
The application allows users to create, search, and book rides, focusing on the core principles of **distributed systems**, **interoperability**, and **security**.

This project was developed as part of the **Distributed Systems** course of the Department of Informatics and Telematics at Harokopio University.

---

## Team
- **It2023045** – Ανδρέας Μητρόπουλος
- **It2023072** – Αντέμ Τζιντόλι
- **It2023093** – Αλέξιος Λίτος

---

## Core Features

### User
- Create rides as a driver
- Search for available rides
- Book and cancel ride seats
- View ride history
- Submit basic user ratings

### Administrator
- User management
- System monitoring
- View basic usage statistics

---

## System Architecture

The application follows:
- **Layered Architecture**
    - Controller Layer
    - Service Layer
    - Repository Layer
- **Hexagonal Architecture (Ports & Adapters)** for external service integration

The system clearly separates:
- **Web UI** (Spring MVC & Thymeleaf)
- **REST API** (JSON-based)

---

## REST API
GreenRide exposes a RESTful API that:
- uses JSON for data exchange,
- applies proper HTTP methods and status codes,
- is fully documented using **OpenAPI / Swagger UI**.

The API can be consumed by external clients or future applications (SPA or mobile).

---

## Security
- **Web UI**: Stateful authentication using Spring Security
- **REST API**: Stateless authentication using **JWT**
- Role-based access control (USER / ADMIN)
- Endpoint protection with appropriate HTTP status codes

---

## External Services
GreenRide consumes external services as **black-box services**:
- **Geolocation / Maps API** for distance calculation and estimated travel time
- **Weather API** for providing supplementary weather information

Integration is implemented via adapters, ensuring low coupling and system extensibility.

---

## Database
- **H2** for development and testing
- Easy migration support to **PostgreSQL**
- **JPA / Hibernate** for persistence
- Core entities: User, Ride, Booking, Rating

---

## How to Run the Application

### Prerequisites
- Java 17+
- Maven

---

## Startup Order (Important)

**Important:** The system consists of two separate applications that must be started in a specific order.

### Start NOC (Network Operations Center)
The **NOC** application must be started **first**, running on port **8081**.

    mvn spring-boot:run

### Start GreenRide
After the NOC service is running, start the **GreenRide** application on port **8080**.

    mvn spring-boot:run

**Note:**  

This startup order is required because **GreenRide communicates with the NOC service**.  
Using different ports allows both applications to run simultaneously on the same machine and reflects a **distributed system architecture**.

## Access URLs

- **Homepage (Web UI)**:  
  [![Homepage](https://img.shields.io/badge/Homepage-Open-green)](http://localhost:8080)


- **Swagger UI (API Documentation)**:  
  [![Swagger](https://img.shields.io/badge/Swagger-API-blue)](http://localhost:8080/swagger-ui.html)
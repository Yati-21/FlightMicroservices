# Flight Booking Microservices System

### An event-driven microservices system built on the Reactive Stack using Spring Boot WebFlux, Spring Cloud, Kafka, and Reactive MongoDB

[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%20|%20WebFlux-brightgreen)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-Reactive%20MongoDB-4EA94B)](https://www.mongodb.com/)
[![Messaging](https://img.shields.io/badge/Messaging-Apache%20Kafka-231F20)](https://kafka.apache.org/)

---

### Table of Contents

* [Architecture Overview](#architecture-overview)
* [Project Links](#project-links)
* [Microservices Overview](#microservices-overview)
* [Key Features](#key-features)
* [Technical Tools](#technical-tools)

---

## Architecture Overview

This project implements a **Reactive Microservices Architecture**, prioritizing high concurrency, non-blocking operations, and service resilience via Circuit Breakers.
<img width="1375" height="752" alt="image" src="https://github.com/user-attachments/assets/8343147e-fdc5-47bb-bd44-dde018bee09b" />

---

## Project Links

* **Config Server Repository:** Centralized configuration files for all microservices.
    [https://github.com/Yati-21/FlightMicroservice-config-server.git](https://github.com/Yati-21/FlightMicroservice-config-server.git)

* **Full Documentation & Screenshots:** Project documentation, API usage screenshots, code coverage reports, SonarQube reports, etc.
    [https://docs.google.com/document/d/1g31-gcR0JVY1Y8ug1mz2hmOaK-C5k7JSfuH5LV1S-Oc/edit?usp=sharing](https://docs.google.com/document/d/1g31-gcR0JVY1Y8ug1mz2hmOaK-C5k7JSfuH5LV1S-Oc/edit?usp=sharing)

---

### High-Level Architecture Summary

The system is organized around a central API Gateway and Service Registry, with asynchronous communication handled by Kafka.

| Component | Role |
| :--- | :--- |
| **API Gateway** (8080) | Single entry point, load balancing, routing (WebFlux). |
| **Booking Service** (9004) | Core transaction logic, publishes events to Kafka. |
| **Flight Service** (9003) | Flight search, inventory adjustments, inter-service calls. |
| **Airline Service** (9001) & **User Service** (9002) | CRUD for master domain entities. |
| **Notification Service** | Kafka Consumer that sends confirmation emails. |
| **Eureka** (8761) & **Config Server** (8888) | Dynamic service discovery and centralized configuration. |

---

## Microservices Overview

The application is decomposed into eight highly cohesive services.

### Infrastructure Services

| Component | Port | Role & Key Features |
| :--- | :--- | :--- |
| **API Gateway** (Spring Cloud Gateway) | `8080` | Handles all incoming HTTP requests & routes them to microservices. **Reactive (WebFlux)**, Load balancing (Eureka), Central entry point. |
| **Eureka Service Registry** | `8761` | All services register and discover each other dynamically. |
| **Config Server** (GitHub-backed) | `8888` | All services fetch their configuration from GitHub via `spring.cloud.config.uri`. Centralized DB configs, ports, credentials, and Kafka topics. |

### Domain Microservices

| Component | Port | Primary Responsibilities | Tools/Interaction |
| :--- | :--- | :--- | :--- | 
| **Airline Service** | `9001` | Handles airline **CRUD** operations | Communicates with API Gateway & Eureka. |
| **User Service** | `9002` | Handles user registration & querying (used for validation) | Communicates with API Gateway & Eureka. |
| **Flight Service** | `9003` | Flight search, inventory seat adjustments, info retrieval | **Feign Client** → Airline Service, **Circuit Breaker** (Resilience4j). |
| **Booking Service** | `9004` | Core business logic (validation, availability, booking) | **Reactive WebClient** + **Circuit Breaker**, **Kafka Producer** (publishes event). |
| **Notification Service** | - | Listens to **Kafka topic `booking-created`** and sends email confirmations | **Kafka Consumer**. |

### Kafka Flow

The system uses Kafka for asynchronous, non-blocking email notifications:

1.  **Booking Service** publishes `booking-created` message
2.  **Kafka Broker** receives the message on the `booking-created` topic
3.  **Notification Service** consumes the message and triggers email dispatch to the user

---

## Key Features

* **Reactive Core:** Built entirely on **Spring Boot WebFlux** for non-blocking I/O and high throughput
* **Resilience:** Implements **Circuit Breaker** patterns (**Resilience4j**) to prevent cascading failures in service communication
* **Event-Driven:** Uses **Kafka** for decoupling the core booking transaction from the notification process
* **Booking System:** Handles seat availability, PNR generation, passenger management, and seat conflict detection
* **Business Rules:** Strong validation and business logic enforced
* **Quality Assurance:** High code coverage (**90%+**) measured by **JaCoCo/SonarQube**

---

## Technical Tools

| Component | Technology | Details |
| :--- | :--- | :--- |
| **Framework** | `Spring Boot WebFlux` | Reactive, Non-blocking architecture |
| **Discovery** | `Eureka Service Registry` | Dynamic service lookup and client-side load balancing |
| **Configuration** | `Spring Cloud Config` | Centralized configuration fetched from **GitHub** |
| **Inter-Service** | `Feign Client` & `Reactive WebClient` | Declarative REST (Feign) and non-blocking reactive client (WebClient) |
| **Resilience** | `Resilience4j` | Circuit Breaker implementation |
| **Messaging** | `Apache Kafka` | Event-driven architecture |
| **Testing** | `Postman Test Suite` & `Newman CLI` | Automated API functional and regression testing |
| **Quality** | `JaCoCo` & `SonarQube` | Code coverage and static code analysis |
| **Load Testing** | `Apache JMeter` | Performance testing for concurrency and throughput |


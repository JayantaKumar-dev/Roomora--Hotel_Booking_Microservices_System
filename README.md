# 🏨 Roomora – Hotel Booking System (Microservices Architecture)

![Java](https://img.shields.io/badge/Java-17-blue) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Microservices](https://img.shields.io/badge/Architecture-Microservices-orange)
![Status](https://img.shields.io/badge/Status-In%20Development-yellow)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

---

## ✨ Overview
**Roomora** is a cloud-ready hotel booking platform built with **Spring Boot Microservices architecture**.
It allows guests, property owners, and admins to interact in a seamless way — from property listing to booking, payments, and notifications. All managed through independent services with centralized service discovery and API gateway.

---

## 👥 Roles & Features
### 🏨 Guests (Customers)

- 🔍 Search and filter hotels/properties by city, date, availability.

- 📅 Book rooms and manage reservations.

- 💳 Make secure payments via Stripe.

- 📩 Receive email/SMS notifications (booking confirmation, updates, etc.).

### 🏠 Property Owners

- 🏗️ Register and list properties with rooms, images, and details.

- 📊 Manage availability and pricing of rooms.

- ✅ Get notified when a booking is made.

- 💵 Receive payments after guest check-in/checkout.

### 🛡️ Admin

- 👨‍💻 Manage and monitor all users, properties, and bookings.

- 📜 View system logs and monitor services through the Admin Server.

- 🔐 Ensure system security and compliance.

- 🧩 Handle exceptions (e.g., fraudulent activities, disputes).

---

## 🏗️ Microservices Overview

| Service | Description |
| :--- | :--- |
| 🖥️ Admin Server | Spring Boot Admin – monitor microservices |
| 🌐 API Gateway | Routes requests to microservices (Spring Cloud Gateway) |
| 🔑 Auth Service | Handles login, signup, JWT authentication |
| 🏡 Property Service | Manage hotels, rooms, availability |
| 📅 Booking Service | Booking creation, availability management |
| 💳 Payment Service | Stripe sandbox integration for payments |
| 📢 Notification Service | Sends email/SMS notifications using Kafka |
| 📡 Service Registry (Eureka) | Service discovery for microservices |

---

```mermaid
flowchart TD
    Client([🧑 User]) -->|Login / Booking| Gateway[🌐 API Gateway]
    Gateway --> Auth[🔑 Auth Service]
    Gateway --> Property[🏡 Property Service]
    Gateway --> Booking[📅 Booking Service]
    Gateway --> Payment[💳 Payment Service]
    Gateway --> Notification[📢 Notification Service]
    
    Auth --> Eureka[📡 Service Registry<br/>Eureka]
    Property --> Eureka
    Booking --> Eureka
    Payment --> Eureka
    Notification --> Eureka
    
    Admin[🖥️ Admin Server] --> Eureka
    
    %% Styling for better visual hierarchy and contrast
    classDef default fill:#ffffff,stroke:#2c3e50,stroke-width:2px,color:#2c3e50;
    classDef service fill:#3498db,stroke:#2980b9,stroke-width:2px,color:#ffffff;
    classDef registry fill:#e74c3c,stroke:#c0392b,stroke-width:2px,color:#ffffff;
    classDef client fill:#2ecc71,stroke:#27ae60,stroke-width:2px,color:#ffffff;
    classDef gateway fill:#9b59b6,stroke:#8e44ad,stroke-width:2px,color:#ffffff;
    
    class Auth,Property,Booking,Payment,Notification,Admin service;
    class Eureka registry;
    class Client client;
    class Gateway gateway;
```
---
# 🚀 Tech Stack

## 🏗️ Architecture & Frameworks  
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2496ED?style=for-the-badge&logo=spring&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)

## 🔍 Service Discovery & API Gateway  
![Netflix Eureka](https://img.shields.io/badge/Netflix%20Eureka-E50914?style=for-the-badge&logo=netflix&logoColor=white)
![Spring Cloud Gateway](https://img.shields.io/badge/Spring%20Cloud%20Gateway-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

## 💾 Database  
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

## 💳 Payments & Integrations  
![Stripe](https://img.shields.io/badge/Stripe-008CDD?style=for-the-badge&logo=stripe&logoColor=white)
![Kafka](https://img.shields.io/badge/Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)

## 📦 Build & Deployment  
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white)

## 🛠️ Development Tools  
![Spring Tool Suite](https://img.shields.io/badge/Spring%20Tool%20Suite-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)



## 🛠️ Tech Stack

**Backend**  
![Java](https://img.shields.io/badge/Java%2017-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Kafka](https://img.shields.io/badge/Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)

**Microservices**  
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Eureka](https://img.shields.io/badge/Eureka-E50914?style=for-the-badge&logo=netflix&logoColor=white)
![API Gateway](https://img.shields.io/badge/API%20Gateway-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

**Database**  
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

**Payment**  
![Stripe](https://img.shields.io/badge/Stripe-008CDD?style=for-the-badge&logo=stripe&logoColor=white)

**Security**  
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

**Tools**  
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![STS](https://img.shields.io/badge/Spring%20Tool%20Suite-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

**Others**  
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)
![Feign Client](https://img.shields.io/badge/Feign%20Client-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Mail Service](https://img.shields.io/badge/Mail%20Service-0078D4?style=for-the-badge&logo=microsoftoutlook&logoColor=white)
![ngrok](https://img.shields.io/badge/ngrok-1F1E37?style=for-the-badge&logo=ngrok&logoColor=white)


## 🏗️ System Architecture
*(Add your diagram here later — for now placeholder)*  


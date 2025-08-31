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

## 🏗️ System Architecture

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

## 🛠️ Tech Stack

**Backend**  
![Java](https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Kafka](https://img.shields.io/badge/Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)

**Microservices**  
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-4EA94B?style=for-the-badge&logo=spring&logoColor=white)
![Netflix Eureka](https://img.shields.io/badge/Netflix%20Eureka-E50914?style=for-the-badge&logo=netflix&logoColor=white)
![Feign Client](https://img.shields.io/badge/Feign%20Client-5C2D91?style=for-the-badge&logo=spring&logoColor=white)
![API Gateway](https://img.shields.io/badge/API%20Gateway-008080?style=for-the-badge&logo=spring&logoColor=white)

**Database**  
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

**Payment**  
![Stripe](https://img.shields.io/badge/Stripe-008CDD?style=for-the-badge&logo=stripe&logoColor=white)

**Security**  
![Spring Security](https://img.shields.io/badge/Spring%20Security-28A745?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)

**Tools**  
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![STS](https://img.shields.io/badge/STS-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white)

**Others**  
![AWS S3](https://img.shields.io/badge/AWS%20S3-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white)
![Mail Service](https://img.shields.io/badge/Mail%20Service-D14836?style=for-the-badge&logo=gmail&logoColor=white)
![ngrok](https://img.shields.io/badge/ngrok-1F1E37?style=for-the-badge&logo=ngrok&logoColor=white)

---

## 🔑 Setup & Installation Guide

Step-by-step:

1. Clone repo.

2. Configure `.env` or `application.yml` for DB & Stripe keys.

3. Run `mvn clean install`.

4. Start services in order (`Eureka → Config → Gateway → Others`).

5. Access API docs (e.g., `http://localhost:8080/swagger-ui`).

---

## 📊 Database Schemas

### 🔐 Microservice: `Auth Service`
**`📝 User Table Schema`**

| Column Name | Data Type | Constraints                 | Description                                        |
| ----------- | --------- | --------------------------- | -------------------------------------------------- |
| id          | BIGINT    | Primary Key, Auto Increment | Unique identifier for each user                    |
| name        | VARCHAR   | NOT NULL                    | Full name of the user                              |
| username    | VARCHAR   | NOT NULL, UNIQUE            | Username used for login                            |
| email       | VARCHAR   | NOT NULL, UNIQUE            | Email address of the user                          |
| password    | VARCHAR   | NULLABLE                    | Encrypted password                                 |
| role        | VARCHAR   | NULLABLE                    | Role of the user (ADMIN / PROPERTY\_OWNER / GUEST) |

###########################################################################################################

### 🏡 Microservice: `Property Service`
1. **`📝 Property Table Schema`**

| Column Name                | Data Type | Description                     |
| -------------------------- | --------- | ------------------------------- |
| `id` (PK)                  | BIGINT    | Unique property ID              |
| `name`                     | VARCHAR   | Name of the property            |
| `number_of_beds`           | INT       | Total beds in the property      |
| `number_of_rooms`          | INT       | Total rooms                     |
| `number_of_bathrooms`      | INT       | Number of bathrooms             |
| `number_of_guests_allowed` | INT       | Max guests allowed              |
| `property_owner_language`  | VARCHAR   | Preferred language of the owner |
| `city_id` (FK)             | BIGINT    | Linked city                     |
| `area_id` (FK)             | BIGINT    | Linked area                     |
| `state_id` (FK)            | BIGINT    | Linked state                    |
| `country_id` (FK)          | BIGINT    | Linked country                  |


2. **`🛏️ Rooms Table Schema`**

| Column Name        | Data Type | Description                                |
| ------------------ | --------- | ------------------------------------------ |
| `id` (PK)          | BIGINT    | Room ID                                    |
| `room_type`        | VARCHAR   | Type of room (Single, Double, Suite, etc.) |
| `base_price`       | DECIMAL   | Base price for booking                     |
| `property_id` (FK) | BIGINT    | Linked property                            |


3. **`📅 Room Availability Table Schema`**

| Column Name       | Data Type | Description                                   |
| ----------------- | --------- | --------------------------------------------- |
| `id` (PK)         | BIGINT    | Availability ID                               |
| `available_date`  | DATE      | Date of availability                          |
| `available_count` | INT       | Number of rooms available on that date        |
| `price`           | DECIMAL   | Price for that date (can override base price) |
| `room_id` (FK)    | BIGINT    | Linked room                                   |


4. **`🖼️ Property Photos Table Schema`**

| Column Name        | Data Type | Description                                                  |
| ------------------ | --------- | ------------------------------------------------------------ |
| `id` (PK)          | BIGINT    | Photo ID                                                     |
| `url`              | VARCHAR   | Image URL (stored in cloud storage like AWS S3 / Cloudinary) |
| `property_id` (FK) | BIGINT    | Linked property                                              |


5. **`🌍 Location Tables`**

- Country

| Column Name | Data Type | Description  |
| ----------- | --------- | ------------ |
| `id` (PK)   | BIGINT    | Country ID   |
| `name`      | VARCHAR   | Country name |

- State

| Column Name       | Data Type | Description    |
| ----------------- | --------- | -------------- |
| `id` (PK)         | BIGINT    | State ID       |
| `name`            | VARCHAR   | State name     |
| `country_id` (FK) | BIGINT    | Linked country |

- City

| Column Name     | Data Type | Description  |
| --------------- | --------- | ------------ |
| `id` (PK)       | BIGINT    | City ID      |
| `name`          | VARCHAR   | City name    |
| `state_id` (FK) | BIGINT    | Linked state |

- Area

| Column Name    | Data Type | Description |
| -------------- | --------- | ----------- |
| `id` (PK)      | BIGINT    | Area ID     |
| `name`         | VARCHAR   | Area name   |
| `city_id` (FK) | BIGINT    | Linked city |


**📌 Entity Relationship Diagram (ERD)**
```mermaid
erDiagram
    Property {
        int property_id PK
        string name
        string description
        string address
        decimal price
        int city_id FK
        int area_id FK
        int state_id FK
        int country_id FK
        datetime created_at
        datetime updated_at
    }
    
    Rooms {
        int room_id PK
        int property_id FK
        string room_type
        decimal price
        int capacity
        string amenities
        datetime created_at
    }
    
    RoomAvailability {
        int availability_id PK
        int room_id FK
        date available_date
        boolean is_available
        decimal price_for_date
    }
    
    PropertyPhotos {
        int photo_id PK
        int property_id FK
        string photo_url
        boolean is_primary
        int display_order
    }
    
    Country {
        int country_id PK
        string country_name
        string country_code
    }
    
    State {
        int state_id PK
        int country_id FK
        string state_name
        string state_code
    }
    
    City {
        int city_id PK
        int state_id FK
        string city_name
        string zip_code
    }
    
    Area {
        int area_id PK
        int city_id FK
        string area_name
        string pincode
    }

    Property ||--o{ Rooms : "has"
    Rooms ||--o{ RoomAvailability : "contains"
    Property ||--o{ PropertyPhotos : "contains"
    
    Property }o--|| City : "located_in"
    Property }o--|| Area : "situated_in"
    Property }o--|| State : "within"
    Property }o--|| Country : "belongs_to"
    
    Country ||--o{ State : "has"
    State ||--o{ City : "contains"
    City ||--o{ Area : "divided_into"

```

###########################################################################################################

### 📦 Microservice: `Booking Service`
1. **`📝 Bookings Table Schema`**

| Column Name             | Data Type    | Constraints               | Description                                   |
| ----------------------- | ------------ | ------------------------- | --------------------------------------------- |
| `id`                    | BIGINT (PK)  | AUTO\_INCREMENT, NOT NULL | Unique booking identifier                     |
| `name`                  | VARCHAR(100) | NOT NULL                  | Guest name                                    |
| `email`                 | VARCHAR(100) | NOT NULL                  | Guest email address                           |
| `mobile`                | VARCHAR(20)  | NOT NULL                  | Guest contact number                          |
| `property_id`           | BIGINT       | NOT NULL                  | Reference to property (from Property Service) |
| `room_id`               | BIGINT       | NOT NULL                  | Reference to specific room                    |
| `property_name`         | VARCHAR      |                           | Name of the booked property                   |
| `room_type`             | VARCHAR      |                           | Type of room (e.g., Deluxe, Standard)         |
| `check_in_date`         | DATE         | NOT NULL                  | Check-in date                                 |
| `check_out_date`        | DATE         | NOT NULL                  | Check-out date                                |
| `total_price`           | DECIMAL      | NOT NULL                  | Total price for the booking                   |
| `payment_status`        | VARCHAR(20)  |                           | Payment status (PENDING, PAID, FAILED)        |
| `transaction_id`        | VARCHAR      |                           | Transaction identifier from payment gateway   |
| `payment_intent_id`     | VARCHAR      |                           | Stripe payment intent ID                      |
| `refund_transaction_id` | VARCHAR      |                           | Refund transaction ID (if cancelled)          |
| `status`                | VARCHAR(20)  |                           | Booking status (CONFIRMED, CANCELLED, etc.)   |
| `total_nights`          | INT          |                           | Total nights of stay                          |
| `created_at`            | TIMESTAMP    | AUTO GENERATED            | Timestamp when booking was created            |
| `updated_at`            | TIMESTAMP    | AUTO GENERATED            | Last updated timestamp                        |


2. **`📅 BookingDate Table Schema`**

| Column Name  | Data Type   | Constraints                        | Description                    |
| ------------ | ----------- | ---------------------------------- | ------------------------------ |
| `id`         | BIGINT (PK) | AUTO\_INCREMENT, NOT NULL          | Unique booking-date identifier |
| `date`       | DATE        | NOT NULL                           | Specific date of the booking   |
| `booking_id` | BIGINT (FK) | NOT NULL, REFERENCES `Bookings.id` | Links to a booking record      |


**📌 Entity Relationship Diagram (ERD)**

```mermaid
erDiagram
    BOOKINGS {
        bigint id PK
        string name
        string email
        string mobile
        bigint property_id FK
        bigint room_id FK
        string property_name
        string room_type
        date check_in_date
        date check_out_date
        decimal total_price
        string payment_status
        string transaction_id
        string payment_intent_id
        string refund_transaction_id
        string status
        int total_nights
        datetime created_at
        datetime updated_at
    }
    
    BOOKING_DATE {
        bigint id PK
        date date
        bigint booking_id FK
    }

    BOOKINGS ||--o{ BOOKING_DATE : "contains"
```

---

## 5️⃣ Access Application

- API Gateway → `http://localhost:5555/`
- Eureka Server → `http://localhost:8761/`
- Admin Server → `http://localhost:8080/`
- Auth Service → `http://localhost:8081/`
- Property Service → `http://localhost:8082/`
- Booking Service → `http://localhost:8084/`
- Payment Service → `http://localhost:8085/`
- Notification Service → `http://localhost:8083/`








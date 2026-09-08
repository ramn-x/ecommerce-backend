E-Commerce Backend

A production-ready E-Commerce REST API built with Java, Spring Boot, Spring Security, JWT, Spring Data JPA, Hibernate, and MySQL.

The project provides secure APIs for authentication, users, products, shopping carts, checkout, orders, payments, validation, exception handling, API documentation, and automated testing.

🚀 Live Deployment

Backend: Railway

Swagger UI:

https://ecommerce-backend-production-1e14.up.railway.app/swagger-ui/index.html

The live URL can change if the Railway domain is regenerated.

✨ Features

Authentication & Security

JWT-based authentication

BCrypt password hashing

Role-based authorization

USER and ADMIN roles

Ownership checks for user-specific resources

Protected REST endpoints with Spring Security

Users

User registration

User login

Admin-only user listing/search operations

User management with access control

Products

Create, read, update, and delete products

Admin-only product creation and modification

Product search

Price filtering

Pagination

Sorting

Request validation for name, price, and quantity

Shopping Cart

Add items to cart

View cart

Update cart items

Delete cart items

Ownership validation for cart access

Checkout & Orders

Checkout from the shopping cart

Create orders from cart items

Automatic stock reduction

Cart clearing after checkout

Order ownership checks

Admin order management

Order lookup by product

Order status workflow:

PENDING

CONFIRMED

SHIPPED

DELIVERED

CANCELLED

Stock restoration when a pending order is cancelled

Validation of invalid order-status transitions

Payments

Payment creation for orders

Duplicate payment prevention

Payment ownership validation

Payment status workflow:

PENDING

PAID

FAILED

REFUNDED

Payment-to-order status integration

Admin payment access

Validation & Exception Handling

Bean Validation with jakarta.validation

Centralized GlobalExceptionHandler

Structured error responses

Custom exceptions for users, products, orders, cart, payments, stock, and authorization

Appropriate HTTP status codes

API Documentation

Swagger/OpenAPI documentation is available through SpringDoc.

/swagger-ui/index.html
/v3/api-docs

Testing

JUnit 5

Spring Boot integration tests

MockMvc API tests

Authentication tests

Authorization tests

Validation tests

Product tests

Cart tests

Checkout tests

Order-status workflow tests

Payment workflow tests

The project currently contains a comprehensive test suite covering the major API flows.

🏗️ Architecture

Client
  │
  ▼
Controller
  │
  ▼
Service
  │
  ▼
Repository
  │
  ▼
MySQL

Supporting layers:

DTO
Mapper
Entity
Security
Exception
Configuration

📁 Project Structure

ecommerce-backend/
├── src/
│   ├── main/
│   │   ├── java/com/ecommerce/ecommerce_backend/
│   │   │   ├── Controller/
│   │   │   ├── DTO/
│   │   │   ├── Entity/
│   │   │   ├── Exception/
│   │   │   ├── Mapper/
│   │   │   ├── Repository/
│   │   │   ├── Security/
│   │   │   ├── Service/
│   │   │   ├── config/
│   │   │   └── EcommerceBackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/com/ecommerce/ecommerce_backend/
│           └── EcommerceBackendApplicationTests.java
├── pom.xml
└── README.md

🛠️ Tech Stack

Technology

Purpose

Java

Programming language

Spring Boot

Backend framework

Spring Web

REST APIs

Spring Security

Authentication and authorization

JWT

Stateless authentication

BCrypt

Password hashing

Spring Data JPA

Data access

Hibernate

ORM

MySQL

Relational database

Maven

Build and dependency management

Swagger / OpenAPI

API documentation

JUnit 5

Testing

MockMvc

API testing

Railway

Deployment

⚙️ Local Setup

1. Clone the repository

git clone https://github.com/ramn-x/ecommerce-backend.git
cd ecommerce-backend

2. Create the MySQL database

CREATE DATABASE ecommerce_db;

3. Configure local database

For development, configure the datasource in application.properties according to your local MySQL installation.

Example:

spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

spring.profiles.active=dev

4. Configure JWT secret

Set the JWT_SECRET environment variable.

PowerShell:

$env:JWT_SECRET="your-local-jwt-secret"

The development profile reads it through:

jwt.secret=${JWT_SECRET}

5. Run the application

Windows:

.\mvnw.cmd spring-boot:run

Or run EcommerceBackendApplication from IntelliJ IDEA.

The application starts on:

http://localhost:8080

🧪 Run Tests

Run the full Maven test suite:

.\mvnw.cmd clean test

🔐 Production Configuration

Production uses environment variables instead of hardcoded database credentials or JWT secrets.

application-prod.properties:

spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.open-in-view=false

jwt.secret=${JWT_SECRET}

Required production variables:

DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
SPRING_PROFILES_ACTIVE=prod

For an initial database bootstrap, Hibernate may temporarily use ddl-auto=update. After the schema is created, production should use ddl-auto=validate or a proper database migration strategy.

📚 Important API Endpoints

Authentication

POST /users
POST /users/login

Users

GET    /users
GET    /users/search
GET    /users/email
GET    /users/{id}
DELETE /users/{id}

Products

GET    /products
GET    /products/{id}
POST   /products
PUT    /products/{id}
DELETE /products/{id}

Cart

POST   /cart
GET    /cart
PUT    /cart/{id}
DELETE /cart/{id}

Orders

POST   /orders
POST   /orders/checkout
GET    /orders
GET    /orders/{id}
GET    /orders/user/{userId}
GET    /orders/product/{productId}
PUT    /orders/{id}
DELETE /orders/{id}

Payments

POST /payments
GET  /payments
GET  /payments/{id}
GET  /payments/order/{orderId}
PUT  /payments/{id}/status

Check Swagger for the current request/response schemas and authorization requirements.

🔑 Authentication Flow

1. Register user
      ↓
2. Login with email + password
      ↓
3. Receive JWT token
      ↓
4. Send token in Authorization header
      ↓
Authorization: Bearer <JWT>

📦 Example Login Request

POST /users/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password"
}

Example response:

{
  "token": "<JWT_TOKEN>"
}

🧭 Order Status Flow

PENDING
   ├──→ CONFIRMED
   │      └──→ SHIPPED
   │             └──→ DELIVERED
   │
   └──→ CANCELLED

💳 Payment Status Flow

PENDING
   ├──→ PAID
   │      └──→ REFUNDED
   │
   └──→ FAILED

🚀 Deployment

The application is deployed on Railway using:

GitHub Repository
        ↓
Railway Spring Boot Service
        ↓
Railway MySQL Service

The Spring Boot service uses a Railway-generated public domain and communicates with MySQL through Railway's internal network.

🔒 Security Notes

Never commit real JWT secrets to GitHub.

Never commit production database passwords.

Use environment variables for sensitive configuration.

Use HTTPS for deployed API access.

Keep production database configuration separate from local development configuration.

Prefer database migrations for controlled production schema changes.

👨‍💻 Author

Ramn-x

GitHub: https://github.com/ramn-x/ecommerce-backend

⭐ Built with Java + Spring Boot + Spring Security + JPA + MySQL.

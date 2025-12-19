# Digital Wallet Backend

A **Java Spring Boot** backend service for the **Digital Wallet** platform.  
Provides REST APIs for user and admin management, wallet operations, transactions, and audit logs.

---

## Features

- **User Authentication & Authorization**
  - Register and login for users and admins
  - JWT-based authentication
  - Role-based access control

- **Wallet Management**
  - Create, view, and manage wallets
  - Deposit and transfer funds
  - Fetch transaction history

- **Admin Features**
  - Admin dashboard metrics (total users, wallets, transactions, audit logs)
  - Audit log tracking
  - Role-specific API endpoints

- **Data Persistence**
  - Uses in-memory H2 database (for development/testing)
  - Entities: Users, Wallets, Transactions, AuditLogs

- **Error Handling**
  - Global exception handling
  - Standardized API error responses

---

## Tech Stack

- **Backend:** Java, Spring Boot
- **Database:** H2 (in-memory)
- **Authentication:** JWT
- **Build Tool:** Maven
- **Testing:** JUnit

---

## Configuration

The backend is configured using `application.yml`:

```yaml
server:
  port: 8080

spring:
  application:
    name: digitalwallet

  datasource:
    url: jdbc:h2:mem:walletdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    root: INFO
    org.springframework.web: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.orm.jdbc.bind: TRACE
    org.system.digitalwallet: DEBUG

```

---

## Installation & Setup

Clone the repository:

git clone https://github.com/devopsam01/backend_digital_wallet.git
cd backend_digital_wallet/


Build and run the application:

mvn clean install
mvn spring-boot:run


The API will run at http://localhost:8080
.


## Folder Structure

```graphql
src/main/java/com/example/digitalwallet/
├── controller/    # REST controllers
├── service/       # Business logic
├── repository/    # JPA repositories
├── model/         # Entity classes
├── dto/           # Request/Response DTOs
├── security/      # JWT and security configuration
└── DigitalWalletApplication.java  # Spring Boot entry point

```



## API Endpoints

- Authentication (/api/auth)
  - Method	Endpoint	Description
  - POST	/register	Register a new user (role optional)
  - POST	/login	Login and receive JWT token
  - POST	/logout	Logout the authenticated user
  - GET	/profile	Get authenticated user profile

- Admin Users (/api/admin/users)
  - Method	Endpoint	Description
  - POST	/register	Register a new admin (first setup or protected later)
  - Admin Dashboard (/api/admin/dashboard)

  - Method	Endpoint	Description
  - GET	/metrics	Fetch admin metrics (users, wallets, transactions, audit logs)
  - Audit Logs (/api/audit)

  - Method	Endpoint	Description
  - GET	/me	Get current user audit logs (paginated)
  - GET	/entity/{entityType}	Get audit logs by entity type (paginated)

- Wallet Management (/api/wallet)
  - Method	Endpoint	Description
  - POST	/create	Create a new wallet
  - GET	/all	Get all wallets for authenticated user
  - GET	/{walletId}	Get a single wallet by ID
  - POST	/topup	Top-up a wallet
  - POST	/transfer	Transfer funds between wallets
GET	/{walletId}/transactions	Get all transactions for a wallet
GET	/transactions/all	Get all transactions for the user

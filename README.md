# SpringBook 📚

SpringBook is a personal book management Spring Boot application.

## Features

- Personal book library
- Reading status and progress tracking
- Book ratings and reviews
- Reading journal and personal notes
- Custom book collections and reading lists

## Tools

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- Flyway
- Maven

## Architecture

The backend is organized by feature, separating responsibilities into entities, repositories, DTOs, and services.

### Project Structure

```text
springbook/
├── src/
│   └── main/
│       ├── java/com/springbook/
│       │   ├── book/
│       │   ├── reading/
│       │   ├── user/
│       │   └── SpringBookApplication.java
│       └── resources/
│           ├── db/migration/
│           └── application.properties
├── pom.xml
└── README.md
```

## Getting Started

_Prerequisites : Java 21, Maven 3.9+, PostgreSQL_

Create a PostgreSQL database named `springbook_db`.

Configure your database connection using the following environment variables:

- `DB_URL` (=jdbc:postgresql://localhost:5432/springbook_db)
- `DB_USERNAME` (=postgres)
- `DB_PASSWORD`

Compile and run the application from the root directory:

```bash
mvn compile
mvn spring-boot:run

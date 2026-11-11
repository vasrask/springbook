# SpringBook

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
- Spring Security
- OAuth2 Resource Server / JWT
- BCrypt password hashing
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
│       │   ├── auth/
│       │   ├── book/
│       │   ├── booklist/
│       │   ├── reading/
│       │   ├── user/
│       │   ├── security/
│       │   ├── config/
│       │   ├── exception/
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
- `JWT_SECRET` (generate a 256-bit or longer random secret)

To generate the JWT secret in PowerShell:
```powershell
  $bytes = New-Object byte[] 32
  $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
  $rng.GetBytes($bytes)
  $secret = [Convert]::ToBase64String($bytes)
  $secret
  $rng.Dispose()
```
_Note: JWT needs to be at least 32 bytes because it is signed with the HS256 algorithm._

Compile and run the application from the root directory:

```bash
mvn compile
mvn spring-boot:run
```

## API Overview

### Authentication

| Method | Endpoint             | Description                    | Authentication |
|--------|----------------------|--------------------------------|----------------|
| POST   | `/api/auth/register` | Register a new user            | Public         |
| POST   | `/api/auth/login`    | Authenticate and receive a JWT | Public         |

### Books

| Method | Endpoint                          | Description           | Authentication |
|--------|-----------------------------------|-----------------------|----------------|
| GET    | `/api/books`                      | Retrieve all books    | Required       |
| POST   | `/api/books`                      | Create a new book     | Required       |
| GET    | `/api/books/{id}`                 | Retrieve a book by ID | Required       |
| GET    | `/api/books/search?title={title}` | Search books by title | Required       |

### Personal Library

| Method | Endpoint                       | Description                               | Authentication |
|--------|--------------------------------|-------------------------------------------|----------------|
| GET    | `/api/library`                 | Retrieve the authenticated user's library | Required       |
| POST   | `/api/library`                 | Add a book to the user's library          | Required       |
| GET    | `/api/library/status/{status}` | Filter library books by reading status    | Required       |

### Reading Journal and Notes

| Method  | Endpoint                       | Description                        | Authentication |
|---------|--------------------------------|------------------------------------|----------------|
| POST    | `/api/notes`                   | Create a reading note              | Required       |
| GET     | `/api/notes/book/{userBookId}` | Retrieve notes for a library entry | Required       |
| GET     | `/api/notes/{id}`              | Retrieve a specific note           | Required       |
| PUT     | `/api/notes/{id}`              | Update a reading note              | Required       |
| DELETE  | `/api/notes/{id}`              | Delete a reading note              | Required       |

### Booklists

| Method  | Endpoint                             | Description                        | Status |
|---------|--------------------------------------|------------------------------------|--------|
| POST    | `/api/lists`                         | Create a collection                | 201    |
| GET     | `/api/lists`                         | Get all your collections           | 200    |
| GET     | `/api/lists/{id}`                    | Get a specific collection          | 200    |
| PUT     | `/api/lists/{id}`                    | Update a collection                | 200    |
| DELETE  | `/api/lists/{id}`                    | Delete a collection                | 204    |
| POST    | `/api/lists/{id}/books`              | Add a book to a collection         | 201    |
| GET     | `/api/lists/{id}/books`              | Get books in a collection          | 200    |
| DELETE  | `/api/lists/{id}/books/{userBookId}` | Remove a book from a collection    | 204    |

### Authentication Header

Protected endpoints require a valid JWT in the `Authorization` header:

```http
Authorization: Bearer <your-token>
```

### Reading Status Values

The following values are supported:

- `WANT_TO_READ`
- `CURRENTLY_READING`
- `READ`
- `ON_HOLD`
- `DROPPED`

### Note types

The following values are supported:

- `GENERAL`
- `CHAPTER`
- `QUOTE`
- `REFLECTION`

_Note: All protected endpoints require authentication; the backend identifies the current user from the JWT._
# Library

REST API for managing a library.

## Features

- Manage books and users
- Create and return book loans
- Create and cancel reservations
- View borrowing reports
- PostgreSQL database

## Environment configuration

This project intentionally keeps a `.env` file to simulate a realistic application setup. That makes the environment closer to a real deployment, while still making it easy to run locally.

Rename `.env.example` to `.env` and fill it with these values:

```env
POSTGRES_DB=library
POSTGRES_USER=user
POSTGRES_PASSWORD=password
```

This ensures the Docker Compose stack and the Java application connect to the same PostgreSQL credentials without issues.

## Run

Requirements: Docker and Docker Compose.

From the project root, run:

```bash
./run.sh
```

The script builds and starts the application and PostgreSQL with Docker Compose. The root endpoint redirects to Swagger UI:

```text
http://localhost:8080/
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

The application health endpoint is available at:

```text
http://localhost:8080/actuator/health
```

The generated OpenAPI document is available at `/v3/api-docs`.

To stop the services, press `Ctrl+C`.

## Main endpoints

- `/api/books`
- `/api/users`
- `/api/loans`
- `/api/reservations`
- `/api/reports`

## Tests

```bash
./mvnw test
```

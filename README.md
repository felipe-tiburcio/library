# Biblioteca

REST API for managing a library.

## Features

- Manage books and users
- Create and return book loans
- Create and cancel reservations
- View borrowing reports
- PostgreSQL database

## Run

Requirements: Docker and Docker Compose.

From the project root, run:

```bash
./run.sh
```

The script builds and starts the application and PostgreSQL with Docker Compose. The API is available at:

```text
http://localhost:8080
```

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

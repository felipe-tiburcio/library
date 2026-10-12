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

The script builds and starts the application and PostgreSQL with Docker Compose. The root endpoint at
http://localhost:8080/ redirects to Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

The application health endpoint is available at:

```text
http://localhost:8080/actuator/health
```

The generated OpenAPI document is available at `/v3/api-docs`.

To stop the services, press `Ctrl+C`.

### Basic development data

The `./run.sh` script starts the application and PostgreSQL with the default seed data. The seed creates five classic books for basic API testing:

- *Alice's Adventures in Wonderland*
- *The Wonderful Wizard of Oz*
- *The Adventures of Pinocchio*
- *Peter Pan*
- *The Hobbit*

### Pagination dataset

To simulate a heavy PostgreSQL database for pagination and performance testing, first start the application with `./run.sh`. Then, in another terminal, run:

```bash
./scripts/load-pagination-data.sh
```

The loader waits for PostgreSQL to become ready, verifies that the application has created the `books` table, and executes [scripts/pagination-data.sql](scripts/pagination-data.sql). The SQL script generates 1,000,000 deterministic books with predictable `title-N` and `author-N` values. Together with the five default books, the database contains more than 1 million books. It marks 80% of the generated books as available and ignores duplicate ISBNs, so the loader can be run repeatedly.

The paginated books endpoint is `GET /api/books`. Pagination and filtering are provided through query parameters:

- `page`: zero-based page number; defaults to `0`.
- `size`: number of books per page; defaults to `20`.
- `sort`: property and direction, such as `sort=title,asc` or `sort=author,desc`.
- `title`: optional title filter.
- `author`: optional author filter.
- `isbn`: optional ISBN filter.
- `available`: optional availability filter (`true` or `false`).

Examples:

```bash
# First page with the default size of 20 books
curl "http://localhost:8080/api/books"

# Third page with 50 books, sorted by title
curl "http://localhost:8080/api/books?page=2&size=50&sort=title,asc"

# Available books matching a title
curl "http://localhost:8080/api/books?available=true&title=title-100"
```

The response is a Spring `Page` containing the current page in `content` and pagination metadata such as `totalElements`, `totalPages`, `number`, and `size`.

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

#!/usr/bin/env bash
set -euo pipefail

dc=(docker compose)

"${dc[@]}" up -d postgres

for attempt in {1..30}; do
    if "${dc[@]}" exec -T postgres sh -c 'pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB"' >/dev/null 2>&1; then
        break
    fi

    if [[ "$attempt" -eq 30 ]]; then
        echo "PostgreSQL did not become ready in time." >&2
        exit 1
    fi

    sleep 1
done

if ! "${dc[@]}" exec -T postgres sh -c \
    'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Atc "SELECT to_regclass('"'"'public.books'"'"')"' \
    | grep -q '^books$'; then
    echo "Table 'books' not found. Start the Spring application first so it can create the schema." >&2
    exit 1
fi

"${dc[@]}" exec -T postgres sh -c 'psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -d "$POSTGRES_DB"' \
    < scripts/pagination-data.sql

echo "Pagination dataset loaded successfully."

#!/usr/bin/env bash
set -euo pipefail

./mvnw compile -DskipTests

(
    inotifywait -m -r -e close_write,create,delete,move src |
    while read -r _; do
        ./mvnw compile -DskipTests
    done
) &

watcher_pid=$!
trap 'kill "$watcher_pid" 2>/dev/null || true' EXIT

exec ./mvnw spring-boot:run -Dspring-boot.run.fork=false -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"

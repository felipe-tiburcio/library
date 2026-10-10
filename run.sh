#!/usr/bin/env bash
set -euo pipefail

exec docker compose up --build --force-recreate --remove-orphans

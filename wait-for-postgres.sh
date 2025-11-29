#!/usr/bin/env bash
set -e

: "${SPRING_DATASOURCE_HOST:=postgres}"
: "${SPRING_DATASOURCE_PORT:=5432}"
: "${SPRING_DATASOURCE_URL:=${SPRING_DATASOURCE_HOST}:${SPRING_DATASOURCE_PORT}}"
: "${SPRING_DATASOURCE_USERNAME:=postgres}"

# Extra: permitir que o usuário sobreponha criando SPRING_DATASOURCE_HOST/PORT no compose/env
HOST=${SPRING_DATASOURCE_HOST}
PORT=${SPRING_DATASOURCE_PORT}
USER=${SPRING_DATASOURCE_USERNAME}

echo "=> Waiting for Postgres at ${HOST}:${PORT} ..."

# Tenta psql (pg_isready é o método mais robusto)
# Se houver senha, pg_isready ainda checa conexão TCP; se acesso precisar de auth, ainda assim OK para disponibilidade.
RETRIES=0
MAX_RETRIES=60
SLEEP_SECONDS=1

while ! pg_isready -h "${HOST}" -p "${PORT}" >/dev/null 2>&1; do
  RETRIES=$((RETRIES+1))
  if [ "$RETRIES" -ge "$MAX_RETRIES" ]; then
    echo "=> Postgres did not become ready in time (${MAX_RETRIES}s). Exiting."
    exit 1
  fi
  printf '.'
  sleep "${SLEEP_SECONDS}"
done

echo
echo "=> Postgres is available. Continuing start."

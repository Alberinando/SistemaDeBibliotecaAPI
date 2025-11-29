#!/bin/sh
# wait-for-postgres.sh
# Espera até o Postgres estar pronto (usa pg_isready). Timeout em segundos via TIMEOUT.
set -e

: "${TIMEOUT:=60}"
: "${SPRING_DATASOURCE_USERNAME:=postgres}"
: "${SPRING_DATASOURCE_PASSWORD:=postgres}"

# Se passar SPRING_DATASOURCE_URL em formato JDBC, tenta extrair host/port/name
if [ -n "${SPRING_DATASOURCE_URL}" ]; then
  # Ex.: jdbc:postgresql://host:5432/dbname?params...
  # extrai host, port e db simples
  DB_HOST=$(echo "$SPRING_DATASOURCE_URL" | sed -n 's#jdbc:postgresql://\([^:/?]*\).*#\1#p')
  DB_PORT=$(echo "$SPRING_DATASOURCE_URL" | sed -n 's#jdbc:postgresql://[^:/?]*:\([0-9]*\).*#\1#p')
  DB_NAME=$(echo "$SPRING_DATASOURCE_URL" | sed -n 's#jdbc:postgresql://[^/]*/\([^?]*\).*#\1#p')
fi

: "${DB_HOST:=localhost}"
: "${DB_PORT:=5432}"
: "${DB_NAME:=postgres}"

echo "Aguardando Postgres em ${DB_HOST}:${DB_PORT} (db=${DB_NAME}) por até ${TIMEOUT}s..."

elapsed=0
while ! pg_isready -h "$DB_HOST" -p "$DB_PORT" -U "$SPRING_DATASOURCE_USERNAME" >/dev/null 2>&1; do
  if [ "$elapsed" -ge "$TIMEOUT" ]; then
    echo "Timeout: Postgres não ficou pronto após ${TIMEOUT}s"
    exit 1
  fi
  sleep 2
  elapsed=$((elapsed+2))
done

echo "Postgres pronto. Prosseguindo."
exec "$@"

# --- builder ---
FROM maven:3.8.6-eclipse-temurin-17 AS builder
LABEL stage=builder
WORKDIR /build

# Copia apenas o essencial para cache de dependências e garante mvnw executável
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN chmod +x ./mvnw
RUN mkdir -p src

# Baixa dependências (offline) - melhora cache entre builds
RUN ./mvnw -B -q dependency:go-offline -DskipTests || mvn -B -q dependency:go-offline -DskipTests

# Copia código-fonte e empacota a aplicação
COPY src ./src
# Gera o jar (skip tests para acelerar)
RUN ./mvnw -B -q clean package -DskipTests || mvn -B -q clean package -DskipTests

# OBS: se seu projeto for multi-module ajuste o path do JAR abaixo conforme necessário.

# --- runtime ---
FROM eclipse-temurin:17-jre-jammy
LABEL stage=runtime
WORKDIR /app

# Instala utilitários (pg_isready/psql) para o wait script
RUN apt-get update \
 && apt-get install -y --no-install-recommends postgresql-client curl ca-certificates \
 && rm -rf /var/lib/apt/lists/*

# Copia JAR gerado do builder
# Atenção: certifique-se que só exista um .jar relevante em target/ ou ajuste o padrão.
COPY --from=builder /build/target/*.jar app.jar

# Copia script que espera o Postgres
COPY wait-for-postgres.sh /app/wait-for-postgres.sh
RUN chmod +x /app/wait-for-postgres.sh

# Configurações runtime
ENV JAVA_OPTS=""
EXPOSE 8088

ENTRYPOINT ["/bin/sh", "-c", "/app/wait-for-postgres.sh && exec java $JAVA_OPTS -jar /app/app.jar"]
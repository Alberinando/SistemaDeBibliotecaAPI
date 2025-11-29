# --- builder ---
FROM maven:3.8.6-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN mkdir -p src
RUN ./mvnw -B -q dependency:go-offline -DskipTests || mvn -B -q dependency:go-offline -DskipTests
COPY src ./src
RUN ./mvnw -B clean package -DskipTests || mvn -B clean package -DskipTests

# Copia apenas o essencial para cache de dependências
COPY pom.xml mvnw ./
COPY .mvn/ .mvn/
RUN mkdir -p src

# Baixa dependências
RUN ./mvnw -B -q dependency:go-offline -DskipTests || mvn -B -q dependency:go-offline -DskipTests

# Copia o código e empacota
COPY src ./src
RUN ./mvnw -B clean package -DskipTests || mvn -B clean package -DskipTests

# --- runtime ---
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends postgresql-client curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*
COPY --from=builder /build/target/*.jar app.jar
COPY wait-for-postgres.sh /app/wait-for-postgres.sh
RUN chmod +x /app/wait-for-postgres.sh
ENV JAVA_OPTS=""
EXPOSE 8088
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8088/actuator/health || exit 1
ENTRYPOINT ["/bin/sh", "-c", "/app/wait-for-postgres.sh && exec java $JAVA_OPTS -jar /app/app.jar"]

# Instala psql / pg_isready (para o script de espera)
RUN apt-get update && apt-get install -y --no-install-recommends postgresql-client curl ca-certificates \
    && rm -rf /var/lib/apt/lists/*

# Copia JAR do build
COPY --from=builder /build/target/*.jar app.jar

# Copia script de espera (vou fornecer o conteúdo abaixo; assegure-se de adicioná-lo ao repositório)
COPY wait-for-postgres.sh /app/wait-for-postgres.sh
RUN chmod +x /app/wait-for-postgres.sh

# Variáveis e porta (podem ser sobrescritas no docker-compose/Coolify)
ENV JAVA_OPTS=""
EXPOSE 8088

# Healthcheck usa actuator; ajusta a porta caso mude
HEALTHCHECK --interval=30s --timeout=5s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:8088/actuator/health || exit 1

ENTRYPOINT ["/bin/sh", "-c", "/app/wait-for-postgres.sh && exec java $JAVA_OPTS -jar /app/app.jar"]

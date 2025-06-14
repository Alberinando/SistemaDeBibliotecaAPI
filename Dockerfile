FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .

RUN apt-get install maven -y
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

EXPOSE 8088

COPY --from=builder /app/target/*.jar application.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

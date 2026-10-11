FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline -DskipTests

COPY src ./src
RUN ./mvnw clean package -DskipTests

FROM maven:3.9-eclipse-temurin-21 AS development

WORKDIR /workspace

RUN apt-get update \
    && apt-get install --no-install-recommends -y inotify-tools \
    && rm -rf /var/lib/apt/lists/*

COPY pom.xml mvnw ./
COPY scripts ./scripts
COPY .mvn .mvn
RUN chmod +x mvnw scripts/dev-entrypoint.sh && ./mvnw dependency:go-offline -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080 5005

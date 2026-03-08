# Build stage - Use Java 21
FROM maven:3.8.4-openjdk-21-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage - Also use Java 21 for consistency
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/estate-management-system.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
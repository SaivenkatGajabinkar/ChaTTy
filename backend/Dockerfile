# =========================================================================
# Stage 1: Build the Spring Boot application using Maven and JDK 17
# =========================================================================
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app

# Copy the pom.xml and download dependencies to leverage Docker layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# =========================================================================
# Stage 2: Create the lightweight execution environment using JRE 17
# =========================================================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy the compiled JAR from the build stage
# Spring Boot's maven plugin names the jar artifact-id-version.jar (chaty-0.0.1-SNAPSHOT.jar)
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (the port Spring Boot runs on by default or via server.port)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]


# Stage 1: Build the Spring Boot application
FROM maven:3.10.0-eclipse-temurin-25 AS build

WORKDIR /app

# Copy Maven project configuration
COPY pom.xml .

# Download dependencies first for caching
RUN mvn -B dependency:go-offline

# Copy application source code
COPY src ./src

# Package Spring Boot executable JAR
RUN mvn -B clean package -DskipTests


# Stage 2: Run the application
FROM eclipse-temurin:25-jre AS runtime

WORKDIR /app

# Create a non-root user
RUN groupadd --system spring \
    && useradd --system --gid spring spring

# Copy only the packaged application
COPY --from=build \
    /app/target/finance-tracker-0.0.1-SNAPSHOT.jar \
    app.jar

RUN chown spring:spring /app/app.jar

USER spring

# Application HTTP port
EXPOSE 8080

# Start Spring Boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# ==========================================
# STAGE 1: Build the Java source code
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy your maven configuration file
COPY java-backend/v1/pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy your actual source code files
COPY java-backend/v1/src ./src

# Compile and package the fresh target JAR directly inside Docker
RUN mvn clean package -DskipTests

# ==========================================
# STAGE 2: Run the application
# ==========================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the freshly compiled JAR file directly from Stage 1
COPY --from=builder /build/target/v1-0.0.1-SNAPSHOT.jar /app/v1-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/v1-0.0.1-SNAPSHOT.jar"]

# ==========================================
# STAGE 1: Build the Java source code
# ==========================================
FROM eclipse-temurin:26-jdk AS builder
WORKDIR /build

# Install Maven in the builder image (Debian-based temurin image)
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

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
FROM eclipse-temurin:26-jre
WORKDIR /app

# Copy the freshly compiled JAR file directly from Stage 1
COPY --from=builder /build/target/v1-0.0.1-SNAPSHOT.jar /app/v1-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/v1-0.0.1-SNAPSHOT.jar"]

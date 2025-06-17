# Step 1: Build the Spring Boot application using Maven
FROM maven:3.9.4-amazoncorretto-17 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Step 2: Run the built JAR using JDK
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/TransactionService-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
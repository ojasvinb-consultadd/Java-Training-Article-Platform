# 1. Build Stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Cache dependencies: Copy only the pom and download deps first
# This makes subsequent builds much faster because this layer only reruns if pom.xml changes
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the source
COPY src ./src
RUN mvn clean package -DskipTests

# 2. Runtime Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Add a non-root user for security (Standard Best Practice)
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# Copy only the built JAR from the build stage
# Explicitly name the JAR or use a static naming convention
COPY --from=build /app/target/*.jar app.jar

# Expose the application port
EXPOSE 8080

# Use array syntax for better signal handling
ENTRYPOINT ["java", "-jar", "app.jar"]
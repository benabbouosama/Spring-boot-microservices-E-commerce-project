# Stage 1: Build the project
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# Stage 2: Create the runtime image
FROM eclipse-temurin:21-jdk
ARG PROJECT_VERSION=0.0.1
WORKDIR /home/app

# Copy the built JAR from the previous stage
COPY --from=build /build/target/order-service-${PROJECT_VERSION}-SNAPSHOT.jar order-service.jar

# Expose port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "order-service.jar"]

# Use an official Maven image with Java 18 as the base image for the build stage
FROM maven:3.8.7-openjdk-18-slim as build

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the project files
COPY pom.xml .
COPY src /usr/src/app/src

# Download the dependencies and package the application
RUN mvn dependency:go-offline -B && \
    mvn clean package -DskipTests

# Use openjdk image for the runtime stage
FROM openjdk:18

# Set working directory for the runtime
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /usr/src/app/target/kush-prj-firefly-submission-1.0-SNAPSHOT-jar-with-dependencies.jar /app/app.jar

# Run the application
ENTRYPOINT ["java","-jar","app.jar"]

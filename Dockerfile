# Use an official OpenJDK runtime as a parent image
FROM openjdk:25-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the Maven wrapper and pom.xml to the container
COPY pom.xml mvnw mvnw.cmd /app/
COPY .mvn /app/.mvn

# Download Maven dependencies (this step is cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy the project source code to the container
COPY src /app/src

# Build the application
RUN ./mvnw package -DskipTests

# Expose the port the application runs on
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "target/clinica-0.0.1-SNAPSHOT.jar"]
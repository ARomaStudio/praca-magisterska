# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/idl-client-1.0-SNAPSHOT.jar /app/idl-client.jar

# Expose the port that your application will run on (if needed)
EXPOSE 8080

# Command to run the JAR file when the container starts
CMD ["java", "-jar", "idl-client.jar"]

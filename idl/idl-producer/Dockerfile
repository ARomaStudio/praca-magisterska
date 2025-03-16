# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/idl-producer-1.0-SNAPSHOT.jar /app/idl-producer.jar

# Expose the port that your application will run on (if needed)
EXPOSE 8082

# Command to run the JAR file when the container starts
CMD ["java", "-jar", "idl-producer.jar"]

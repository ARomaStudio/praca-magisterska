FROM maven:3.9-eclipse-temurin-21 AS builder

# Install protobuf compiler
RUN apt-get update && apt-get install -y protobuf-compiler

# Build producer
WORKDIR /producer
COPY idl-producer/pom.xml .
COPY idl-producer/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /producer/target/*.jar producer.jar

EXPOSE 8082

# Run both applications using the Java command
CMD java -jar producer.jar 
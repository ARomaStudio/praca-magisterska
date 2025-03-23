FROM maven:3.9-eclipse-temurin-21 AS builder

# Build producer
WORKDIR /producer
COPY rest-producer/pom.xml .
COPY rest-producer/src ./src
RUN mvn clean package -DskipTests

# Build consumer
WORKDIR /consumer
COPY rest-consumer/pom.xml .
COPY rest-consumer/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

# Install necessary tools
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    procps \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=builder /producer/target/*.jar producer.jar
COPY --from=builder /consumer/target/*.jar consumer.jar
COPY monitor-and-run.sh .

# Create directory for JFR files
RUN mkdir /desktop && \
    chmod +x monitor-and-run.sh

EXPOSE 8081 8082

# Run applications with JFR monitoring
CMD ["./monitor-and-run.sh"] 
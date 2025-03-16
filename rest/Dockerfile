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

WORKDIR /app
COPY --from=builder /producer/target/*.jar producer.jar
COPY --from=builder /consumer/target/*.jar consumer.jar
COPY start-services.sh .
RUN chmod +x start-services.sh

EXPOSE 8082
CMD ["./start-services.sh"] 
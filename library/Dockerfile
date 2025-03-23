FROM maven:3.9-eclipse-temurin-21 AS builder

# Build producer
WORKDIR /producer
COPY library-producer/pom.xml .
COPY library-producer/src ./src
RUN mvn clean package -DskipTests

# Build client
WORKDIR /client
COPY library-cilent/pom.xml .
COPY library-cilent/src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app
COPY --from=builder /producer/target/*.jar producer.jar
COPY --from=builder /client/target/*.jar client.jar
COPY monitor-and-run.sh .

# Create directory for JFR files
RUN mkdir /desktop && \
    chmod +x monitor-and-run.sh

EXPOSE 8081 8082

# Run applications with JFR monitoring
CMD ["./monitor-and-run.sh"] 
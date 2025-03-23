FROM maven:3.9-eclipse-temurin-21 AS builder

# Install protoc and required dependencies
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    wget \
    unzip \
    libatomic1 \
    && rm -rf /var/lib/apt/lists/*

# Download and install protoc for Linux
RUN wget https://github.com/protocolbuffers/protobuf/releases/download/v3.7.0/protoc-3.7.0-linux-x86_64.zip && \
    unzip protoc-3.7.0-linux-x86_64.zip -d /usr/local && \
    rm protoc-3.7.0-linux-x86_64.zip

# Build producer
WORKDIR /producer
COPY idl-producer/pom.xml .
COPY idl-producer/src ./src
RUN mvn clean package -DskipTests

# Build client
WORKDIR /client
COPY idl-client/pom.xml .
COPY idl-client/src ./src
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
COPY --from=builder /client/target/*.jar client.jar
COPY monitor-and-run.sh .

# Create directory for JFR files
RUN mkdir /desktop && \
    chmod +x monitor-and-run.sh

EXPOSE 8081 8082

# Run applications with JFR monitoring
CMD ["./monitor-and-run.sh"] 
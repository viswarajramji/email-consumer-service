# Email Consumer Service

A Spring Boot microservice that consumes email events from Apache Kafka and provides real-time statistics about unique emails and domains.

## Overview

This service listens to email events published to a Kafka topic and maintains statistics about:
- Unique email addresses
- Unique email domains
- Time-windowed statistics for recent events

## Features

- **Kafka Integration**: Consumes email events from `email-events` topic
- **Real-time Statistics**: Tracks unique emails and domains
- **Time-windowed Queries**: Get statistics for recent time periods
- **Automatic Cleanup**: Removes old entries to prevent memory issues
- **REST API**: Exposes statistics via HTTP endpoints
- **OpenAPI Documentation**: Swagger UI available for API exploration

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.0**
- **Spring Kafka**
- **Apache Kafka**
- **Lombok**
- **Jackson** (JSON processing)
- **SpringDoc OpenAPI** (API documentation)
- **Maven** (build tool)

## Prerequisites

- Java 17 or higher
- Apache Kafka running on `localhost:9092`
- Maven 3.6+ (or use included Maven wrapper)

## Quick Start

### 1. Start Kafka

Ensure Apache Kafka is running on your local machine:

```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka Server
bin/kafka-server-start.sh config/server.properties
```

### 2. Create Kafka Topic

Create the required Kafka topic:

```bash
bin/kafka-topics.sh --create --topic email-events --bootstrap-server localhost:9092 --partitions 1 --replication-factor 1
```

### 3. Run the Application

Using Maven wrapper:
```bash
./mvnw spring-boot:run
```

Or using Maven:
```bash
mvn spring-boot:run
```

The application will start on port `8084`.

## API Endpoints

### Get Overall Statistics
```http
GET /api/stats
```

**Response:**
```json
{
  "uniqueEmailCount": 150,
  "uniqueDomainCount": 25
}
```

### Get Recent Statistics
```http
GET /api/stats/recent?lastSeconds=300
```

**Parameters:**
- `lastSeconds`: Number of seconds to look back from current time

**Response:**
```json
{
  "uniqueEmailCount": 45,
  "uniqueDomainCount": 12
}
```

## Email Event Format

The service expects email events in the following JSON format:

```json
{
  "email": "user@example.com",
  "eventTime": "2024-01-15T10:30:00Z"
}
```

## Configuration

### Application Properties

```properties
spring.application.name=EmailConsumerService
server.port=8084
```

### Kafka Configuration

The service is configured to:
- Connect to Kafka at `localhost:9092`
- Consume from topic `email-events`
- Use consumer group `email-stats-group`
- Start reading from earliest offset

## Architecture

### Components

1. **KafkaEventConsumer**: Listens to Kafka messages and processes email events
2. **EmailEventStatsService**: Maintains statistics using in-memory concurrent maps
3. **EmailStatsController**: Exposes REST endpoints for querying statistics
4. **CleanupSchedulerService**: Periodically removes old entries (runs every minute)

### Data Storage

The service uses in-memory `ConcurrentHashMap` collections to store:
- Email addresses with their last seen timestamps
- Domain names with their last seen timestamps

### Cleanup Strategy

- Cleanup runs every 60 seconds
- Removes entries older than 30 minutes
- Prevents memory leaks from long-running instances

## Testing the Service

### 1. Send Test Messages to Kafka

```bash
# Start Kafka console producer
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic email-events

# Send test messages
{"email": "john@example.com", "eventTime": "2024-01-15T10:30:00Z"}
{"email": "jane@example.com", "eventTime": "2024-01-15T10:31:00Z"}
{"email": "bob@company.com", "eventTime": "2024-01-15T10:32:00Z"}
```

### 2. Check Statistics

```bash
# Get overall statistics
curl http://localhost:8084/api/stats

# Get recent statistics (last 5 minutes)
curl "http://localhost:8084/api/stats/recent?lastSeconds=300"
```

## API Documentation

Once the application is running, you can access the Swagger UI at:
```
http://localhost:8084/swagger-ui.html
```

## Building and Packaging

### Build JAR
```bash
./mvnw clean package
```

### Run JAR
```bash
java -jar target/email-consumer-service-0.0.1-SNAPSHOT.jar
```

## Development

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/example/email/
│   │       ├── config/          # Kafka configuration
│   │       ├── consumer/        # Event consumers
│   │       ├── controller/      # REST controllers
│   │       ├── event/           # Event models
│   │       ├── scheduler/       # Cleanup scheduler
│   │       └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/example/email/
```

### Key Classes

- `EmailEvent`: Record representing an email event
- `KafkaEventConsumer`: Kafka message consumer
- `EmailEventStatsServiceImpl`: Statistics calculation and storage
- `EmailStatsController`: REST API endpoints
- `CleanupSchedulerServiceImpl`: Periodic cleanup task

## Configuration Options

You can customize the following by modifying the source code:

- **Kafka Bootstrap Servers**: Change `BOOTSTRAP_SERVERS` in `KafkaConfig`
- **Topic Name**: Modify `@KafkaListener` annotation in `KafkaEventConsumer`
- **Consumer Group**: Update `groupId` in `@KafkaListener` annotation
- **Cleanup Interval**: Modify `fixedRate` in `CleanupSchedulerServiceImpl`
- **Cleanup Threshold**: Change `CLEANUP_THRESHOLD_SECONDS` constant
- **Server Port**: Update `server.port` in `application.properties`

## Monitoring and Logging

The application uses SLF4J with Logback for logging. Key log events include:
- Successful message consumption
- Message parsing errors
- Cleanup operations

## Limitations

- **In-Memory Storage**: Statistics are lost on application restart
- **Single Instance**: No distributed coordination between multiple instances
- **Memory Usage**: Grows with unique emails/domains until cleanup

## Future Enhancements

- Add persistent storage (Redis, Database)
- Implement distributed coordination for multiple instances
- Add metrics and monitoring (Micrometer/Prometheus)
- Add more sophisticated error handling and retry mechanisms
- Implement rate limiting and backpressure handling

## License

This project is licensed under the MIT License - see the LICENSE file for details.
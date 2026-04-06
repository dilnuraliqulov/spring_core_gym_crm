# ActiveMQ Integration for Training Workload Service

## Overview
This document describes the asynchronous messaging setup using ActiveMQ to replace synchronous REST communication between the Gym CRM service and the Training Workload microservice.

## Architecture

### Previous (REST/Feign Client):
```
Gym CRM Service --[REST/HTTP]--> Training Workload Service
```

### Current (ActiveMQ):
```
Gym CRM Service --[JMS/ActiveMQ Queue]--> Training Workload Service
```

## Components

### 1. Message Producer (`TrainingWorkloadMessageProducer.java`)
- Location: `src/main/java/com/gymcrm/messaging/`
- Sends `TrainingWorkloadMessage` to ActiveMQ queue
- Queue name: `training-workload-queue`

### 2. JMS Configuration (`JmsConfig.java`)
- Location: `src/main/java/com/gymcrm/config/`
- Configures JMS connection factory, message converter (Jackson JSON), and listener factory
- Uses `MappingJackson2MessageConverter` for JSON serialization

### 3. Message DTO (`TrainingWorkloadMessage.java`)
- Location: `src/main/java/com/gymcrm/dto/message/`
- Contains training workload data with transactionId for tracking

### 4. Updated Service (`WorkloadNotificationServiceImpl.java`)
- Now uses `TrainingWorkloadMessageProducer` instead of Feign client
- Sends asynchronous messages to ActiveMQ

## Configuration

### application.yaml
```yaml
spring:
  activemq:
    broker-url: ${ACTIVEMQ_BROKER_URL:tcp://localhost:61616}
    user: ${ACTIVEMQ_USER:admin}
    password: ${ACTIVEMQ_PASSWORD:admin}
    packages:
      trust-all: true
  jms:
    pub-sub-domain: false
```

### Environment Variables
| Variable | Default | Description |
|----------|---------|-------------|
| ACTIVEMQ_BROKER_URL | tcp://localhost:61616 | ActiveMQ broker URL |
| ACTIVEMQ_USER | admin | ActiveMQ username |
| ACTIVEMQ_PASSWORD | admin | ActiveMQ password |

## Docker Setup

ActiveMQ is included in `docker-compose.yaml`:

```yaml
activemq:
  image: apache/activemq-artemis:latest
  container_name: activemq
  ports:
    - "61616:61616"  # JMS port
    - "8161:8161"    # Web console
  environment:
    ARTEMIS_USER: admin
    ARTEMIS_PASSWORD: admin
```

### Access ActiveMQ Console
- URL: http://localhost:8161
- Username: admin
- Password: admin

## Message Format

```json
{
  "_type": "com.gymcrm.dto.message.TrainingWorkloadMessage",
  "trainerUsername": "john.doe",
  "trainerFirstName": "John",
  "trainerLastName": "Doe",
  "isActive": true,
  "trainingDate": "2026-03-06",
  "trainingDuration": 60,
  "actionType": "ADD",
  "transactionId": "uuid-transaction-id"
}
```

## Consumer (Training Workload Service)

The Training Workload microservice should implement a JMS listener:

```java
@Component
@Slf4j
public class TrainingWorkloadMessageConsumer {

    @JmsListener(destination = "training-workload-queue")
    public void receiveWorkloadMessage(TrainingWorkloadMessage message) {
        log.info("Received workload message: {}", message);
        // Process the training workload update
    }
}
```

## Benefits of ActiveMQ vs REST

1. **Asynchronous Processing**: Main service doesn't wait for workload service response
2. **Reliability**: Messages are persisted until consumed
3. **Decoupling**: Services don't need to know each other's location
4. **Retry Capability**: Failed messages can be reprocessed from dead-letter queue
5. **Load Leveling**: Messages are queued during high load periods

## Running Locally

1. Start ActiveMQ:
   ```bash
   docker-compose up activemq -d
   ```

2. Start the application:
   ```bash
   mvn spring-boot:run
   ```

3. Verify ActiveMQ connection in logs:
   ```
   INFO  o.s.jms.connection.CachingConnectionFactory - Successfully started ActiveMQ connection
   ```


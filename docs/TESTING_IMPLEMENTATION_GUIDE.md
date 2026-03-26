# Gym CRM Testing Implementation Guide

## Overview

This guide covers the **Component** and **Integration** BDD tests for the **Gym CRM** microservice using Cucumber framework following BDD best practices.

## Test Architecture

### Test Layers

1. **Component Tests** (`@component` tag)
   - Test individual controllers and services in isolation
   - Use MockMvc for HTTP testing
   - Mock external dependencies (other services, message brokers)
   - Focus on business logic validation

2. **Integration Tests** (`@integration` tag)
   - Test Gym CRM interactions with other services (Training Workload)
   - Test end-to-end workflows
   - Include ActiveMQ messaging validation
   - Focus on cross-service contracts

## Project Structure

```
src/test/
├── java/com/gymcrm/bdd/
│   ├── component/
│   │   ├── ComponentCucumberSpringConfig.java      # @CucumberContextConfiguration + @SpringBootTest
│   │   ├── GymCrmComponentCucumberTest.java        # @Suite @Cucumber runner
│   │   └── stepdefs/
│   │       ├── AuthenticationComponentSteps.java   # Login, role-based access
│   │       ├── TraineeComponentSteps.java          # Trainee CRUD + trainer assignment
│   │       ├── TrainerComponentSteps.java          # Trainer registration + profile
│   │       ├── TrainingControllerSteps.java        # Training session management
│   │       └── TrainingTypeComponentSteps.java     # Training type CRUD
│   ├── integration/
│   │   ├── IntegrationCucumberSpringConfig.java    # @CucumberContextConfiguration + @SpringBootTest
│   │   ├── GymCrmIntegrationCucumberTest.java      # @Suite @Cucumber runner
│   │   └── stepdefs/
│   │       └── MicroservicesIntegrationSteps.java  # Gym CRM + Workload Service messaging
│   └── common/
│       ├── CommonHooks.java                         # @Before @After lifecycle hooks
│       ├── CommonTestConfig.java                    # Shared beans
│       └── TestDataBuilder.java                     # Fluent test data factory
└── resources/
    ├── application-test.yaml                        # H2 + embedded ActiveMQ config
    └── features/gymcrm/
        ├── @component authentication.feature
        ├── @component trainee_management.feature
        ├── @component trainer_management.feature
        ├── @component training_management.feature
        ├── @component training_type_management.feature
        └── @integration microservices_integration.feature
```

## Key Configuration Details

### ComponentCucumberSpringConfig.java

```java
@CucumberContextConfiguration          // Cucumber discovers Spring context from this class
@SpringBootTest                         // Full Spring Boot application context
@AutoConfigureMockMvc                   // Provides MockMvc bean for HTTP testing
@ActiveProfiles("test")                 // Uses application-test.yaml
public class ComponentCucumberSpringConfig {
    @MockBean                           // Mock external service dependencies
    private TrainingEntityService trainingEntityService;
    // ... other mocks ...
}
```

**Why mocks?**
- Component tests isolate a single microservice
- External services (workload, discovery) are mocked
- Allows fast, deterministic testing without external dependencies

### IntegrationCucumberSpringConfig.java

```java
@CucumberContextConfiguration
@SpringBootTest                         // Full application context
@AutoConfigureMockMvc
@ActiveProfiles("test")                 // H2 + embedded ActiveMQ
public class IntegrationCucumberSpringConfig {
    // NO mocks for external services
    // Tests real integration paths
}
```

**Why no mocks?**
- Integration tests validate cross-service communication
- ActiveMQ messages are real (in-memory broker)
- Tests ensure contract compliance between services

### CommonHooks.java

```java
public class CommonHooks {
    @Before(order = 100)
    public void beforeScenario() {
        cleanDatabase();  // Fresh state for each scenario
    }
}
```

### Test Runners

**Component Cucumber Test:**
```java
@Suite
@Cucumber
@ConfigurationParameter(key = "cucumber.features", value = "classpath:features/gymcrm")
@ConfigurationParameter(key = "cucumber.glue", value = "com.gymcrm.bdd.component,com.gymcrm.bdd.common")
@ConfigurationParameter(key = "cucumber.tags", value = "@component")
public class GymCrmComponentCucumberTest {}
```

**Integration Cucumber Test:**
```java
@Suite
@Cucumber
@ConfigurationParameter(key = "cucumber.features", value = "classpath:features/gymcrm")
@ConfigurationParameter(key = "cucumber.glue", value = "com.gymcrm.bdd.integration,com.gymcrm.bdd.common")
@ConfigurationParameter(key = "cucumber.tags", value = "@integration")
public class GymCrmIntegrationCucumberTest {}
```

## Running Tests

### Compile Tests
```bash
mvn clean test-compile
```

### Run All Tests
```bash
mvn clean test verify
```

### Run Component Tests Only
```bash
mvn clean test -Dgroups=component
# OR if using JUnit tags:
mvn test -Dtest="GymCrmComponentCucumberTest"
```

### Run Integration Tests Only
```bash
mvn clean verify -Dgroups=integration
# OR if using JUnit tags:
mvn verify -Dtest="GymCrmIntegrationCucumberTest"
```

### Run Specific Feature File
```bash
mvn test -Dtest="GymCrmComponentCucumberTest" -Dcucumber.features="src/test/resources/features/gymcrm/authentication.feature"
```

## Feature Files & Scenarios

### 1. Authentication (`@component`)

**File:** `src/test/resources/features/gymcrm/authentication.feature`

**Positive Scenarios:**
- ✅ Successfully authenticate with valid credentials
- ✅ Successfully authorize access with valid token
- ✅ Successfully access resource with appropriate role

**Negative Scenarios:**
- ❌ Fail to authenticate with invalid password
- ❌ Fail to authenticate with non-existent user
- ❌ Fail to authenticate inactive user
- ❌ Fail to access protected resource without token
- ❌ Fail to access resource with expired token
- ❌ Fail to access admin resource without admin role

### 2. Trainee Management (`@component`)

**File:** `src/test/resources/features/gymcrm/trainee_management.feature`

**Positive Scenarios:**
- ✅ Successfully register a new trainee
- ✅ Successfully get trainee profile
- ✅ Successfully update trainee profile
- ✅ Successfully deactivate trainee
- ✅ Successfully retrieve trainee trainers list
- ✅ Successfully add trainer to trainee
- ✅ Successfully remove trainer from trainee

**Negative Scenarios:**
- ❌ Fail to register trainee with missing data
- ❌ Fail to register trainee with invalid date of birth
- ❌ Fail to get profile for non-existent trainee
- ❌ Fail to update trainee without authentication

### 3. Trainer Management (`@component`)

**File:** `src/test/resources/features/gymcrm/trainer_management.feature`

**Positive Scenarios:**
- ✅ Successfully register a new trainer
- ✅ Successfully get trainer profile
- ✅ Successfully update trainer profile
- ✅ Successfully deactivate/activate trainer
- ✅ Successfully retrieve trainer trainees list

**Negative Scenarios:**
- ❌ Fail to register trainer with missing data
- ❌ Fail to register trainer with invalid specialization
- ❌ Fail to get profile for non-existent trainer
- ❌ Fail to duplicate username registration

### 4. Training Management (`@component`)

**File:** `src/test/resources/features/gymcrm/training_management.feature`

**Positive Scenarios:**
- ✅ Successfully add a training session
- ✅ Valid training with proper authentication
- ✅ Training notification to workload service

**Negative Scenarios:**
- ❌ Reject training due to invalid credentials
- ❌ Reject training due to missing request fields

### 5. Training Type Management (`@component`)

**File:** `src/test/resources/features/gymcrm/training_type_management.feature`

**Positive Scenarios:**
- ✅ Successfully retrieve all training types
- ✅ Successfully retrieve training type by id
- ✅ Successfully create a new training type
- ✅ Successfully update training type

**Negative Scenarios:**
- ❌ Fail to retrieve non-existent training type
- ❌ Fail to create training type with missing name
- ❌ Fail to create duplicate training type
- ❌ Fail to update non-existent training type

### 6. Microservices Integration (`@integration`)

**File:** `src/test/resources/features/gymcrm/microservices_integration.feature`

**Positive Scenarios:**
- ✅ Successfully add training and notify workload service
- ✅ Successfully update workload after training
- ✅ Successfully delete training and update workload

**Negative Scenarios:**
- ❌ Workload service handles failed training notifications gracefully
- ❌ Workload service validates trainer credentials from Gym CRM

## Test Data Configuration

### application-test.yaml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:gymcrm-test;DB_CLOSE_DELAY=-1;MODE=PostgreSQL
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
  activemq:
    broker-url: vm://embedded?broker.persistent=false
    in-memory: true
```

**Database:** In-memory H2 (fast, isolated tests)
**Message Broker:** Embedded ActiveMQ (no external dependency)
**Flyway:** Disabled in tests (Hibernate manages schema)

## Step Definition Best Practices

### 1. Use Data Tables for Test Data

```gherkin
Scenario: Create multiple training types
  Given the following training types exist:
    | id | typeName  |
    | 1  | Yoga      |
    | 2  | Fitness   |
  When I request all training types
  Then the response should contain 2 training types
```

### 2. Use Parameterized Steps

```gherkin
Scenario Outline: Test authentication with different users
  When I authenticate with username "<username>" and password "<password>"
  Then the response status should be <status>

  Examples:
    | username | password | status |
    | trainer1 | pass123  | 200    |
    | trainer1 | invalid  | 401    |
```

### 3. Separate Positive and Negative Scenarios

```gherkin
# Positive scenarios cluster
Scenario: Successfully register trainee
  ...

# Negative scenarios cluster
Scenario: Fail to register with missing data
  ...
```

## Dependencies & Versions

```xml
<!-- Cucumber -->
<dependency>
  <groupId>io.cucumber</groupId>
  <artifactId>cucumber-java</artifactId>
  <version>7.15.0</version>
  <scope>test</scope>
</dependency>

<!-- JUnit Platform (for @Suite runner) -->
<dependency>
  <groupId>org.junit.platform</groupId>
  <artifactId>junit-platform-suite-api</artifactId>
  <version>1.10.2</version>
  <scope>test</scope>
</dependency>

<!-- Spring Boot Test -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>

<!-- Mock & Stub -->
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>5.6.0</version>
  <scope>test</scope>
</dependency>
```

## Troubleshooting

### Issue: MockMvc bean not found
**Solution:** Ensure step classes do NOT have @SpringBootTest @AutoConfigureMockMvc annotations. These should ONLY be in ComponentCucumberSpringConfig.

### Issue: CucumberContextConfiguration fails
**Solution:** @CucumberContextConfiguration should only be used in Cucumber Config classes, NOT in step definitions.

### Issue: Active MQ connection fails
**Solution:** Check application-test.yaml has `broker-url: vm://embedded?broker.persistent=false`

### Issue: Tests timeout on scenario
**Solution:** Increase timeout in CommonHooks or use `@await` utility in steps.

## CI/CD Integration

**Maven Failsafe Plugin Configuration** (in pom.xml):

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-failsafe-plugin</artifactId>
  <version>3.2.5</version>
  <executions>
    <execution>
      <goals>
        <goal>integration-test</goal>
        <goal>verify</goal>
      </goals>
      <configuration>
        <includes>
          <include>**/*IT.java</include>
          <include>**/*CucumberTest.java</include>
        </includes>
      </configuration>
    </execution>
  </executions>
</plugin>
```

**GitHub Actions Example:**

```yaml
- name: Run Component Tests
  run: mvn test -Dtest="GymCrmComponentCucumberTest"

- name: Run Integration Tests
  run: mvn verify -Dtest="GymCrmIntegrationCucumberTest"
```

## Next Steps

1. ✅ Component tests for Gym CRM implemented and running
2. ⏳ Integration tests for Gym CRM + Training Workload messaging
3. ⏳ Performance tests (load testing with k6 or JMeter)
4. ⏳ API Gateway routing tests
5. ⏳ Discovery Service registration/deregistration tests

## References

- [Cucumber Java Documentation](https://cucumber.io/docs/cucumber/)
- [Spring Boot Testing Guide](https://spring.io/guides/gs/testing-web/)
- [JUnit Platform Suite Documentation](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite)
- [BDD Best Practices](https://specflow.org/bdd/getting-started/#writing-good-scenarios)


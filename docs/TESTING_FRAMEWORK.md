# Gym CRM Testing Framework - BDD with Cucumber

This document provides a comprehensive guide to the testing framework implemented for the Gym CRM application using Cucumber and BDD (Behavior Driven Development) approach.

## Overview

The testing framework covers:
1. **Component Tests** - Testing individual microservice components in isolation using `@component` tag
2. **Integration Tests** - Testing interactions between Gym CRM and Training Workload services using `@integration` tag

Both test suites follow the BDD approach using Cucumber with feature files written in Gherkin language and execute via JUnit Platform Suite engine.

## Architecture

### Test Runners
- **GymCrmComponentCucumberTest**: Runs all scenarios tagged with `@component`
- **GymCrmIntegrationCucumberTest**: Runs all scenarios tagged with `@integration`

Both runners use JUnit Platform Suite with Cucumber engine to discover and execute feature files.

### Spring Context Configuration
- **ComponentCucumberSpringConfig**: Provides Spring Boot test context with `@CucumberContextConfiguration` for component tests with mocked services
- **IntegrationCucumberSpringConfig**: Provides Spring Boot test context with `@CucumberContextConfiguration` for integration tests with real service interaction

### Glue Path Configuration
The glue paths are configured to include:
1. Step definition packages (e.g., `com.gymcrm.bdd.component.stepdefs`)
2. Config package (e.g., `com.gymcrm.bdd.component`)
3. Common hooks package (`com.gymcrm.bdd.common`)

This ensures Cucumber discovers both the `@CucumberContextConfiguration` and step definitions.

## Project Structure

```
src/test/
├── java/com/gymcrm/bdd/
│   ├── component/
│   │   ├── ComponentCucumberSpringConfig.java      # Spring config for component tests (@CucumberContextConfiguration)
│   │   ├── GymCrmComponentCucumberTest.java        # Component test runner (JUnit Platform Suite)
│   │   └── stepdefs/
│   │       ├── TrainingControllerSteps.java        # Training management steps
│   │       ├── TraineeComponentSteps.java          # Trainee management steps
│   │       ├── TrainerComponentSteps.java          # Trainer management steps
│   │       ├── AuthenticationComponentSteps.java   # Authentication steps
│   │       └── TrainingTypeComponentSteps.java     # Training type management steps
│   ├── integration/
│   │   ├── IntegrationCucumberSpringConfig.java    # Spring config for integration tests (@CucumberContextConfiguration)
│   │   ├── GymCrmIntegrationCucumberTest.java      # Integration test runner (JUnit Platform Suite)
│   │   └── stepdefs/
│   │       └── MicroservicesIntegrationSteps.java  # Integration steps
│   └── common/
│       ├── CommonHooks.java                         # Shared test hooks (database cleanup, setup)
│       ├── CommonTestConfig.java                    # Common test configuration (TestDataBuilder, ObjectMapper)
│       └── TestDataBuilder.java                     # Test data factory with fluent builder API
└── resources/features/gymcrm/
    ├── training_management.feature                  # @component - Training feature scenarios
    ├── trainee_management.feature                   # @component - Trainee feature scenarios
    ├── trainer_management.feature                   # @component - Trainer feature scenarios
    ├── authentication.feature                       # @component - Authentication feature scenarios
    ├── training_type_management.feature             # @component - Training type feature scenarios
    └── microservices_integration.feature            # @integration - Microservices integration scenarios
```

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Only Component Tests
```bash
mvn clean test -Dgroups="component"
```

Or filter by test runner:
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest
```

### Run Only Integration Tests
```bash
mvn clean test -Dtest=GymCrmIntegrationCucumberTest
```

### Run Specific Feature
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest#authentication
```

## Feature Files

### Component Tests (Isolated Component Testing)

#### 1. Training Management (`training_management.feature`)

**Positive Scenarios:**
- Successfully add a training session
- Valid training with proper authentication
- Training notification to workload service
- List trainings for trainee/trainer

**Negative Scenarios:**
- Reject training due to invalid credentials
- Reject training due to missing request fields
- Reject training due to invalid training parameters

#### 2. Trainee Management (`trainee_management.feature`)

**Positive Scenarios:**
- Register new trainee successfully
- Get trainee profile by username
- Update trainee profile
- Deactivate/Activate trainee account
- Retrieve trainee trainers list
- Add trainer to trainee
- Remove trainer from trainee

**Negative Scenarios:**
- Fail to register with missing data (firstName, lastName, dateOfBirth)
- Fail to register with invalid date of birth (future date)
- Fail to get profile for non-existent trainee (404)
- Fail to update trainee without authentication (401)
- Fail to update with invalid data
- Fail to add non-existent trainer
- Fail to remove unassigned trainer

#### 3. Trainer Management (`trainer_management.feature`)

**Positive Scenarios:**
- Register new trainer successfully
- Get trainer profile by username
- Update trainer profile
- Deactivate/Activate trainer account
- Retrieve trainer trainees list
- Retrieve trainer workload summary

**Negative Scenarios:**
- Fail to register with missing data
- Fail to register with invalid specialization
- Fail to get profile for non-existent trainer (404)
- Fail to update trainer without authentication (401)
- Fail to register duplicate username

#### 4. Authentication & Authorization (`authentication.feature`)

**Positive Scenarios:**
- Successfully authenticate with valid credentials (200, token returned)
- Successfully authorize access with valid token (200)
- Access resource with appropriate role (200)
- Admin can access admin resources (200)

**Negative Scenarios:**
- Fail to authenticate with invalid password (401)
- Fail to authenticate with non-existent user (401)
- Fail to authenticate inactive user (401)
- Fail to access protected resource without token (401)
- Fail to access resource with expired token (401)
- Fail to access admin resource without admin role (403)
- Fail to access trainer resource as trainee (403)

#### 5. Training Type Management (`training_type_management.feature`)

**Positive Scenarios:**
- Get all training types (200)
- Get training type by id (200)
- Create new training type (201)
- Update training type (200)

**Negative Scenarios:**
- Fail to get non-existent training type (404)
- Fail to create training type without authentication (401)
- Fail to update with invalid data (400)

### Integration Tests (Microservice-to-Microservice Testing)

#### Microservices Integration (`microservices_integration.feature`) - @integration tag

**Positive Scenarios:**
- Gym CRM successfully notifies Training Workload of new training
- Notification includes all required fields (traineeUsername, trainerUsername, trainingDate, duration, actionType)
- Multiple trainingings processed sequentially
- Workload service updates training workload correctly

**Negative Scenarios:**
- Notification fails if Training Workload service is unavailable (with retry mechanism)
- Invalid training data is rejected before notification
- Duplicate notifications are handled idempotently

## Test Configuration

### application-test.yaml
Configures test environment:
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:gymcrm-test       # In-memory H2 database
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: update
  activemq:
    broker-url: vm://embedded?broker.persistent=false  # Embedded broker
```

### ComponentCucumberSpringConfig
```java
@CucumberContextConfiguration  // Tells Cucumber to use this as Spring context
@SpringBootTest                // Loads full application context
@AutoConfigureMockMvc          // Provides MockMvc bean for HTTP testing
@ActiveProfiles("test")        // Uses application-test.yaml
public class ComponentCucumberSpringConfig {
    @MockBean                  // Mock services for isolation
    private TrainingEntityService trainingEntityService;
    // ... other mocked services
}
```

### IntegrationCucumberSpringConfig
```java
@CucumberContextConfiguration  // Tells Cucumber to use this as Spring context
@SpringBootTest                // Loads full application context
@AutoConfigureMockMvc          // Provides MockMvc bean for HTTP testing
@ActiveProfiles("test")        // Uses application-test.yaml
public class IntegrationCucumberSpringConfig {
    // No mock beans - uses real service implementations
}
```

## Best Practices Implemented

1. **Single CucumberContextConfiguration**: Each test runner has exactly one `@CucumberContextConfiguration` annotated class
2. **Glue Path Management**: Glue paths include both step definitions and config package
3. **Service Isolation**: Component tests use `@MockBean` to isolate services
4. **Integration Testing**: Integration tests use real beans to verify service interaction
5. **BDD Approach**: Feature files use Gherkin with Given-When-Then structure
6. **Test Data Builder**: TestDataBuilder provides fluent API for test data creation
7. **Common Hooks**: Shared setup/teardown logic in CommonHooks
8. **Scenario Tagging**: `@component` and `@integration` tags for filtering
9. **No Duplicate Annotations**: Step definition classes don't have duplicate @SpringBootTest annotations
10. **Proper Maven Configuration**: Surefire/Failsafe plugins configured for test execution

## Debugging Failed Tests

### Issue: "Please annotate a glue class with some context configuration"
**Cause**: Cucumber cannot find a class annotated with `@CucumberContextConfiguration`
**Solution**: 
- Ensure glue path includes the config package (e.g., `com.gymcrm.bdd.component`)
- Verify the config class has `@CucumberContextConfiguration` annotation
- Check that glue parameter in test runner includes the config package

### Issue: "Could not autowire MockMvc"
**Cause**: MockMvc bean not available in step definition
**Solution**:
- Ensure `@AutoConfigureMockMvc` is on the CucumberContextConfiguration class
- Verify step definitions don't have duplicate `@SpringBootTest` annotations
- Make sure MockMvc is autowired in step definitions

### Issue: Tests don't run
**Cause**: Runner not being detected or test filter mismatch
**Solution**:
- Verify runner class name ends with `CucumberTest` (matches surefire pattern)
- Check that feature files have appropriate `@component` or `@integration` tags
- Ensure tags in ConfigurationParameter match feature file tags

## Dependencies

### Versions Used
- Spring Boot: 3.2.5
- Cucumber: 7.15.0
- JUnit Platform: 1.10.2
- MockMvc: Built-in with spring-boot-starter-test
- H2 Database: Built-in with spring-boot-starter-test

### Key Dependencies (see pom.xml)
```xml
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-java</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-spring</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.cucumber</groupId>
    <artifactId>cucumber-junit-platform-engine</artifactId>
    <version>${cucumber.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.platform</groupId>
    <artifactId>junit-platform-suite-api</artifactId>
    <version>1.10.2</version>
    <scope>test</scope>
</dependency>
```

## Continuous Integration

### CI Pipeline Configuration
Tests run in the following order:
1. Unit Tests (traditional unit tests)
2. Component Tests (`GymCrmComponentCucumberTest`)
3. Integration Tests (`GymCrmIntegrationCucumberTest`)
4. Coverage reporting

### Health Checks
- All tests pass before merge
- Code coverage > 70%
- No flaky tests (tests run consistently)



## Project Structure

```
src/test/
├── java/com/gymcrm/bdd/
│   ├── component/
│   │   ├── ComponentCucumberSpringConfig.java      # Spring config for component tests
│   │   ├── GymCrmComponentCucumberTest.java        # Component test runner
│   │   └── stepdefs/
│   │       ├── TrainingControllerSteps.java        # Training management steps
│   │       ├── TraineeComponentSteps.java          # Trainee management steps
│   │       ├── TrainerComponentSteps.java          # Trainer management steps
│   │       ├── AuthenticationComponentSteps.java   # Authentication steps
│   │       └── TrainingTypeComponentSteps.java     # Training type management steps
│   └── integration/
│       ├── IntegrationCucumberSpringConfig.java    # Spring config for integration tests
│       ├── GymCrmIntegrationCucumberTest.java      # Integration test runner
│       └── stepdefs/
│           └── MicroservicesIntegrationSteps.java  # Integration steps
└── resources/features/gymcrm/
    ├── training_management.feature                  # Training feature scenarios
    ├── trainee_management.feature                   # Trainee feature scenarios
    ├── trainer_management.feature                   # Trainer feature scenarios
    ├── authentication.feature                       # Authentication feature scenarios
    ├── training_type_management.feature             # Training type feature scenarios
    └── microservices_integration.feature            # Microservices integration scenarios
```

## Feature Files

### 1. Training Management (`training_management.feature`)

**Positive Scenarios:**
- Successfully add a training session
- Valid training with proper authentication
- Training notification to workload service

**Negative Scenarios:**
- Reject training due to invalid credentials
- Reject training due to missing request fields
- Invalid training parameters validation

### 2. Trainee Management (`trainee_management.feature`)

**Positive Scenarios:**
- Register new trainee
- Get trainee profile
- Update trainee profile
- Deactivate trainee
- Retrieve trainee trainers list
- Add trainer to trainee
- Remove trainer from trainee

**Negative Scenarios:**
- Fail to register with missing data
- Invalid date of birth
- Non-existent trainee profile access
- Unauthorized updates
- Non-existent trainer assignment
- Remove unassigned trainer

### 3. Trainer Management (`trainer_management.feature`)

**Positive Scenarios:**
- Register new trainer
- Get trainer profile
- Update trainer profile
- Deactivate/Activate trainer
- Retrieve trainer trainees list
- Retrieve trainer workload summary

**Negative Scenarios:**
- Fail to register with missing data
- Invalid specialization
- Non-existent trainer profile access
- Unauthorized updates
- Duplicate username registration

### 4. Authentication & Authorization (`authentication.feature`)

**Positive Scenarios:**
- Authenticate with valid credentials
- Authorize access with valid token
- Access resource with appropriate role

**Negative Scenarios:**
- Invalid password
- Non-existent user
- Inactive user authentication
- Missing authentication token
- Expired token
- Insufficient permissions (forbidden access)

### 5. Training Type Management (`training_type_management.feature`)

**Positive Scenarios:**
- Retrieve all training types
- Retrieve training type by ID
- Create new training type
- Update training type

**Negative Scenarios:**
- Non-existent training type access
- Missing required fields
- Duplicate training type
- Invalid data validation

### 6. Microservices Integration (`microservices_integration.feature`)

**Positive Scenarios:**
- Notify workload service on training addition
- Notify workload service on training update
- Notify workload service on training deletion
- Handle multiple trainers in single update
- Retry failed notifications

**Negative Scenarios:**
- Fail when workload service is down
- Invalid trainer update
- Race condition handling
- Workload service error handling
- Transaction rollback on notification failure

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Component Tests Only
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest
```

### Run Integration Tests Only
```bash
mvn clean verify -Dit.test=GymCrmIntegrationCucumberTest
```

### Run Specific Feature File
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest -Dcucumber.filter.tags="@trainee"
```

### Run with Detailed Report
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest -Dcucumber.plugin="html:target/cucumber-report.html"
```

## Test Execution Flow

### Component Tests Flow

1. **Setup Phase**
   - Clean database
   - Create test users and entities
   - Mock external dependencies

2. **Test Phase**
   - Execute HTTP requests via MockMvc
   - Validate responses and status codes
   - Verify business logic

3. **Teardown Phase**
   - Clean up test data
   - Reset mocks

### Integration Tests Flow

1. **Service Setup**
   - Start Gym CRM service
   - Mock Workload service
   - Initialize database

2. **Integration Phase**
   - Execute end-to-end workflows
   - Verify service-to-service communication
   - Validate notification delivery

3. **Verification Phase**
   - Check database state
   - Verify workload updates
   - Validate error handling

## Key Step Definitions

### TraineeComponentSteps
- `the_database_is_clean()` - Clear all test data
- `a_trainee_with_username_exists()` - Create test trainee
- `i_register_a_new_trainee()` - Register new trainee
- `i_request_trainee_profile_for()` - Get trainee profile
- `i_update_trainee_profile_with()` - Update trainee
- `i_deactivate_trainee()` - Deactivate trainee
- `i_add_trainer_to_trainee()` - Assign trainer
- `i_remove_trainer_from_trainee()` - Unassign trainer

### TrainerComponentSteps
- `a_trainer_with_username_exists()` - Create test trainer
- `i_register_a_new_trainer()` - Register new trainer
- `i_request_trainer_profile_for()` - Get trainer profile
- `i_update_trainer_profile_with()` - Update trainer
- `i_deactivate_trainer()` - Deactivate trainer
- `i_activate_trainer()` - Activate trainer
- `i_request_trainees_list_for_trainer()` - Get trainer trainees
- `i_request_workload_summary_for_trainer()` - Get workload

### AuthenticationComponentSteps
- `i_authenticate_with_username_and_password()` - Login
- `i_request_protected_resource_with_valid_token()` - Access protected resource
- `i_request_protected_resource_without_authentication()` - Unauthorized access
- `the_token_has_expired()` - Mock token expiry

### TrainingTypeComponentSteps
- `the_following_training_types_exist()` - Create test training types
- `i_request_all_training_types()` - Get all types
- `i_request_training_type_with_id()` - Get type by ID
- `i_create_a_new_training_type()` - Create new type
- `i_update_training_type_with_name()` - Update type

### MicroservicesIntegrationSteps
- `trainee_adds_a_training_with_trainer()` - Create training
- `the_training_is_updated_with_new_duration()` - Update training
- `the_training_is_deleted()` - Delete training
- `the_workload_service_should_receive_add_notification_for_trainer()` - Verify notification
- `the_trainer_workload_in_workload_service_should_be_increased_by_minutes()` - Check workload

## BDD Best Practices Applied

### 1. Declarative Scenarios
- Scenarios describe "what" not "how"
- Feature files are readable by non-technical stakeholders
- Each scenario has clear Given-When-Then structure

### 2. Reusable Steps
- Step definitions are generic and reusable
- Data-driven scenarios with tables
- Parameterized steps for flexibility

### 3. Both Positive and Negative Paths
- Happy path scenarios validate success
- Negative scenarios validate error handling
- Edge cases are covered

### 4. Clear Naming
- Feature and scenario names are descriptive
- Step names follow "Subject-Action-Result" pattern
- Method names match Gherkin keywords

### 5. Proper Test Isolation
- Database cleanup before each test
- Mocked external dependencies
- Independent test data per scenario

## Assertions and Validations

### HTTP Response Validation
```gherkin
Then the response status should be 200
And the error message should contain "error text"
```

### Entity State Validation
```gherkin
Then the trainee should be created successfully
And the trainee should be active
```

### Business Logic Validation
```gherkin
Then the trainee should have trainer "trainer1"
And the trainer specialization should be "Fitness"
```

### Workload Notification Validation
```gherkin
Then the workload service should receive ADD notification for trainer "trainer1"
And the trainer workload should be increased by 60 minutes
```

## Error Handling Scenarios

### Authentication Errors
- Invalid credentials (401 Unauthorized)
- Missing authentication headers (401)
- Expired tokens (401)
- Insufficient permissions (403 Forbidden)

### Validation Errors
- Missing required fields (400 Bad Request)
- Invalid data formats (400)
- Constraint violations (409 Conflict)

### Resource Errors
- Non-existent resources (404 Not Found)
- Duplicate records (409 Conflict)

### Service Integration Errors
- Workload service unavailable (503 Service Unavailable)
- Failed notifications (retry logic)
- Transaction rollback on failure (500)

## Test Data Management

### Database Seeding
- Test data created via step definitions
- Cleanup after each test (@Before/@After hooks)
- Isolated test scenarios

### Fixture Management
- Users created with consistent credentials
- Trainers and trainees properly linked
- Training types pre-populated

### Mock Data
- Random IDs generated for uniqueness
- Timestamps for temporal data
- Realistic business data

## Continuous Integration

Tests are configured to run in CI/CD pipeline:
- Maven Surefire plugin for unit/component tests
- Maven Failsafe plugin for integration tests
- Cucumber reports generated in `target/cucumber-reports/`

### CI Configuration (pom.xml)
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*Test.java</include>
        </includes>
    </configuration>
</plugin>

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
        <includes>
            <include>**/*IT.java</include>
            <include>**/*CucumberTest.java</include>
        </includes>
    </configuration>
</plugin>
```

## Extending Tests

### Adding New Feature
1. Create `.feature` file in `src/test/resources/features/gymcrm/`
2. Implement step definitions in appropriate `Stepdefs` class
3. Follow naming conventions and BDD patterns
4. Add both positive and negative scenarios

### Adding New Step Definition
1. Implement step method with appropriate annotation (@Given, @When, @Then)
2. Keep steps small and focused
3. Reuse existing steps when possible
4. Add proper error messages for failures

### Integration with New Service
1. Create integration feature file
2. Mock external service dependencies
3. Verify request/response contracts
4. Test error scenarios and retries

## Troubleshooting

### MockMvc Not Found Error
- Ensure `@AutoConfigureMockMvc` annotation is present
- Verify Spring context is properly configured
- Check `@SpringBootTest` annotation

### Step Not Implemented
- Verify step definition method signature matches feature file step
- Check package location is in glue path
- Ensure method annotations are correct

### Transaction Rollback Issues
- Verify `@Transactional` on test methods if needed
- Check database schema matches entity definitions
- Ensure test profile has proper datasource configuration

### Test Data Conflicts
- Run database cleanup in `@Before` hooks
- Use unique identifiers for test entities
- Isolate tests to prevent cross-contamination

## Performance Considerations

- Component tests: ~100-200ms per test
- Integration tests: ~200-500ms per test
- Total test suite execution: ~30-60 seconds
- Parallel execution possible with proper isolation

## Maintenance

### Regular Updates
- Review and update feature files when requirements change
- Refactor step definitions to improve readability
- Remove obsolete test scenarios
- Update documentation

### Code Quality
- Keep step definitions DRY (Don't Repeat Yourself)
- Use meaningful variable names
- Add comments for complex logic
- Follow team coding standards

## Conclusion

This comprehensive testing framework ensures high-quality, maintainable code with clear business requirements expressed as executable tests. The BDD approach facilitates communication between technical and non-technical stakeholders while providing robust regression prevention.


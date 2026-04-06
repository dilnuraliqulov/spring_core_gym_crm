# Gym CRM BDD Testing - Quick Start Guide

## Overview

This guide helps you quickly get started with the BDD testing framework for the Gym CRM application using Cucumber.

## What is BDD?

**Behavior Driven Development (BDD)** is a testing approach that:
- Focuses on the behavior of the system from the user's perspective
- Uses plain English (Gherkin) to describe scenarios
- Bridges communication between technical and non-technical stakeholders
- Automates acceptance criteria

## Project Structure

```
├── src/test/
│   ├── java/com/gymcrm/bdd/
│   │   ├── component/          # Component tests (isolated service testing)
│   │   ├── integration/        # Integration tests (service-to-service)
│   │   └── common/             # Shared utilities and hooks
│   └── resources/features/
│       └── gymcrm/             # Gherkin feature files
└── docs/
    ├── TESTING_FRAMEWORK.md    # Detailed documentation
    └── BDD_QUICK_START.md      # This file
```

## Getting Started

### 1. Understanding Feature Files

Feature files use **Gherkin** syntax - plain English with keywords:

```gherkin
Feature: User Authentication
  As a user
  I want to authenticate with my credentials
  So that I can access the system securely

  Scenario: Successfully authenticate with valid credentials
    Given I have valid credentials
    When I submit the login form
    Then I should be authenticated
    And I should receive an authentication token
```

### 2. Gherkin Keywords

| Keyword | Purpose | Example |
|---------|---------|---------|
| `Feature` | Describes a high-level functionality | Feature: User Authentication |
| `Scenario` | Describes a specific test case | Scenario: Login with valid credentials |
| `Given` | Setup/precondition | Given I have valid credentials |
| `When` | Action/trigger | When I submit the login form |
| `Then` | Expected result/assertion | Then I should be authenticated |
| `And` | Additional step of any type | And I should receive a token |
| `Background` | Common setup for all scenarios | Background: Database is initialized |

### 3. Running Your First Test

#### Run all component tests:
```bash
mvn clean test
```

#### Run specific feature file:
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest
```

#### Run a specific scenario:
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest -Dcucumber.filter.tags="@smoke"
```

#### See detailed output:
```bash
mvn clean test -X
```

## Common Workflow

### Writing a New Feature

**Step 1: Create Feature File**
```
src/test/resources/features/gymcrm/my_feature.feature
```

```gherkin
Feature: My New Feature
  
  Scenario: Positive scenario
    Given some precondition
    When I perform an action
    Then I expect a result
    
  Scenario: Negative scenario
    Given some precondition
    When I perform an invalid action
    Then I expect an error
```

**Step 2: Run the Feature**
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest
```

**Step 3: Cucumber Will Suggest Step Definitions**
The console output will show something like:
```
Step definitions:
@Given("some precondition")
public void some_precondition() {
    // TODO: implement me
}
```

**Step 4: Implement Step Definitions**
Add the steps to an appropriate `*Steps.java` class:

```java
@Given("some precondition")
public void some_precondition() {
    // Your test code here
}

@When("I perform an action")
public void i_perform_an_action() {
    // Your test code here
}

@Then("I expect a result")
public void i_expect_a_result() {
    // Your assertion here
}
```

**Step 5: Run Again and Fix**
```bash
mvn clean test -Dtest=GymCrmComponentCucumberTest
```

## Key Test Patterns

### Pattern 1: API Request Testing

```gherkin
When I make a POST request to "/api/users" with data:
  | firstName | John  |
  | lastName  | Smith |
Then the response status should be 201
And the response should contain a user ID
```

### Pattern 2: Database State Verification

```gherkin
Given a user "john" exists in the database
When I deactivate the user
Then the user should be marked as inactive
And the user count should remain the same
```

### Pattern 3: Error Handling

```gherkin
When I try to access a non-existent resource
Then the response status should be 404
And the error message should contain "not found"
```

### Pattern 4: Authentication Flow

```gherkin
Given I am not authenticated
When I access a protected resource
Then the response status should be 401
And I should be redirected to the login page
```

## Feature Files Available

### 1. Training Management
**File:** `training_management.feature`
- Adding training sessions
- Validation and error handling
- Workload notifications

**Example Scenario:**
```gherkin
Scenario: Successfully add a training session
  Given a valid training add request
  When the client submits the training
  Then the response status should be 200
  And the workload service should be notified with action "ADD"
```

### 2. Trainee Management
**File:** `trainee_management.feature`
- Register trainees
- Update trainee profiles
- Manage trainer assignments

**Example Scenario:**
```gherkin
Scenario: Successfully register a new trainee
  Given I have valid trainee registration data
  When I register a new trainee
  Then the trainee should be created successfully
  And the response status should be 201
```

### 3. Trainer Management
**File:** `trainer_management.feature`
- Register trainers
- Manage specializations
- View trainer workloads

### 4. Authentication
**File:** `authentication.feature`
- User login/logout
- Token management
- Authorization checks

### 5. Training Types
**File:** `training_type_management.feature`
- CRUD operations on training types
- Validation rules

### 6. Microservices Integration
**File:** `microservices_integration.feature`
- Service-to-service communication
- Workload synchronization
- Error recovery

## Common Assertions

### HTTP Status
```gherkin
Then the response status should be 200
Then the response status should be 404
Then the response status should be 401
```

### Entity State
```gherkin
Then the user should be created successfully
Then the user should be active
Then the user should not have trainer "trainer1"
```

### Response Content
```gherkin
And the response should contain "success message"
And the error message should contain "required field"
```

### Collection Size
```gherkin
Then the response should contain 5 items
And the trainers list should contain "trainer1"
```

## Best Practices

### 1. Write Clear Scenarios
❌ **Bad:**
```gherkin
Scenario: Test
  Given data
  When action
  Then result
```

✅ **Good:**
```gherkin
Scenario: Successfully create trainee with valid data
  Given I have trainee registration data with firstName "Alice"
  When I submit the registration
  Then the trainee should be created with ID
```

### 2. Include Both Positive and Negative Cases
```gherkin
Scenario: Successfully register trainee (positive)
  ...

Scenario: Fail to register trainee with missing firstName (negative)
  ...
```

### 3. Use Data-Driven Tests
```gherkin
Scenario Outline: Register users with different roles
  Given a user with role <role>
  When I register the user
  Then the user should have <role> permissions

  Examples:
    | role    |
    | ADMIN   |
    | TRAINER |
    | TRAINEE |
```

### 4. Reuse Steps
```gherkin
# Good - reusable steps
Given a user "john" exists
Given a user "jane" exists

# Rather than
Given the following users exist:
  | username |
  | john     |
  | jane     |
```

### 5. Keep Scenarios Independent
Each scenario should be able to run standalone without depending on other scenarios' data.

## Debugging Tips

### 1. View Full Test Output
```bash
mvn clean test -X 2>&1 | tee test-output.log
```

### 2. Run Single Scenario
Add `@skip` to other scenarios temporarily:
```gherkin
@skip
Scenario: Other test
  ...

Scenario: Test to debug
  ...
```

### 3. Add Debug Print Statements
```java
@When("I perform action")
public void i_perform_action() {
    System.out.println("DEBUG: About to perform action");
    // your code
    System.out.println("DEBUG: Action performed");
}
```

### 4. Check Test Database State
Add a debug step to query the database:
```gherkin
Then I should see the data in database
```

```java
@Then("I should see the data in database")
public void i_should_see_the_data_in_database() {
    List<User> users = userRepository.findAll();
    System.out.println("Users in DB: " + users);
}
```

## Troubleshooting

### Issue: "Step is not defined"
**Solution:**
- Verify step definition method name matches feature file exactly
- Check package is in glue path configuration
- Restart IDE to refresh classpath

### Issue: "MockMvc could not be autowired"
**Solution:**
- Add `@AutoConfigureMockMvc` annotation
- Ensure `@SpringBootTest` is present
- Check test profile is active

### Issue: "Database not cleaned between tests"
**Solution:**
- Check `@Before` hook is in glue path
- Verify database cleanup in `CommonHooks` class
- Add explicit `given the database is clean` step

### Issue: "Test hangs/times out"
**Solution:**
- Check for database locks
- Verify service mocks are configured
- Look for infinite loops in step definitions

## Next Steps

1. **Read detailed documentation:** Check `docs/TESTING_FRAMEWORK.md` for comprehensive guide
2. **Explore existing features:** Review feature files in `src/test/resources/features/gymcrm/`
3. **Study step implementations:** Look at step definitions in `src/test/java/com/gymcrm/bdd/`
4. **Run tests:** Execute `mvn clean test` and observe behavior
5. **Write your first feature:** Create a new feature file and implement scenarios
6. **Integrate in CI/CD:** Configure tests in your pipeline

## Resources

### External Learning
- [Cucumber Documentation](https://cucumber.io/docs/cucumber/)
- [Gherkin Syntax](https://cucumber.io/docs/gherkin/reference/)
- [BDD Best Practices](https://cucumber.io/docs/bdd/)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)

### Internal Documentation
- `docs/TESTING_FRAMEWORK.md` - Complete testing guide
- `docs/ACTIVEMQ_INTEGRATION.md` - Message queue integration
- Test code comments - Inline documentation

## Support

For questions or issues:
1. Check existing test scenarios for similar patterns
2. Review error messages carefully - they often indicate the solution
3. Consult the troubleshooting section above
4. Read relevant documentation files

## Summary

The BDD testing framework provides:
✅ Clear, readable test scenarios  
✅ Comprehensive coverage of happy and sad paths  
✅ Easy maintenance and updates  
✅ Communication bridge between stakeholders  
✅ Automated regression prevention  
✅ Component and integration test separation  

Happy testing! 🎉


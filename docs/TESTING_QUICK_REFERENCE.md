# Gym CRM BDD Testing - Quick Reference

## 🚀 Quick Start

### Compile & Run Tests
```bash
# Compile tests only
mvn clean test-compile

# Run all tests (component + integration)
mvn clean test verify

# Run component tests only
mvn test -Dtest="GymCrmComponentCucumberTest"

# Run integration tests only  
mvn verify -Dtest="GymCrmIntegrationCucumberTest"
```

## 📋 Test Structure

```
gym-crm/
├── Component Tests (@component tag)
│   ├── GymCrmComponentCucumberTest.java
│   └── stepdefs/
│       ├── AuthenticationComponentSteps.java
│       ├── TraineeComponentSteps.java
│       ├── TrainerComponentSteps.java
│       ├── TrainingControllerSteps.java
│       └── TrainingTypeComponentSteps.java
├── Integration Tests (@integration tag)
│   ├── GymCrmIntegrationCucumberTest.java
│   └── stepdefs/
│       └── MicroservicesIntegrationSteps.java
├── Common
│   ├── CommonHooks.java
│   ├── CommonTestConfig.java
│   └── TestDataBuilder.java
└── Feature Files
    ├── @component authentication.feature
    ├── @component trainee_management.feature
    ├── @component trainer_management.feature
    ├── @component training_management.feature
    ├── @component training_type_management.feature
    └── @integration microservices_integration.feature
```

## ✅ Test Coverage Summary

| Feature | Component | Integration | Positive | Negative |
|---------|-----------|-------------|----------|----------|
| Authentication | ✅ | ✅ | 3 | 7 |
| Trainee Management | ✅ | ✅ | 7 | 7 |
| Trainer Management | ✅ | ✅ | 6 | 7 |
| Training Management | ✅ | ✅ | 2 | 1 |
| Training Types | ✅ | ✅ | 4 | 7 |
| Microservices Integration | ❌ | ✅ | 3 | 2 |
| **TOTAL** | **51** | **Multiple** | **25** | **31** |

## 🔧 Configuration

### Test Profile: application-test.yaml
- **Database:** H2 in-memory (fast, isolated)
- **Message Broker:** Embedded ActiveMQ
- **Dialect:** PostgreSQL (for compatibility)
- **Schema:** Auto-created by Hibernate

### Component Tests
- **Scope:** Single service
- **Mocking:** External services mocked
- **Speed:** ~50ms per scenario
- **Use:** Unit + API testing

### Integration Tests
- **Scope:** Cross-service messaging
- **Mocking:** None (real integration)
- **Speed:** ~500ms-1s per scenario
- **Use:** Contract testing

## 📝 Gherkin Syntax

### Basic Structure
```gherkin
@component
Feature: Feature Name
  As a user role
  I want to do something
  So that I achieve a goal

  Background:
    Given setup data

  Scenario: Positive scenario name
    Given preconditions
    When I perform action
    Then I verify result

  Scenario: Negative scenario name
    Given invalid preconditions
    When I perform action
    Then I get error
```

### Data Tables
```gherkin
Scenario: Create multiple items
  Given the following items exist:
    | id | name    |
    | 1  | Item 1  |
    | 2  | Item 2  |
  When I request all items
  Then response contains 2 items
```

### Scenario Outline
```gherkin
Scenario Outline: Test with multiple values
  When I login with "<username>" and "<password>"
  Then response status is <status>

  Examples:
    | username | password | status |
    | admin    | pass123  | 200    |
    | invalid  | wrong    | 401    |
```

## 🐛 Troubleshooting

| Issue | Solution |
|-------|----------|
| MockMvc not found | Remove @SpringBootTest from step classes |
| CucumberContextConfiguration error | Use only in config classes, not steps |
| ActiveMQ connection failed | Check vm://embedded broker URL in config |
| Test timeout | Increase wait time or use Awaitility |
| Feature not found | Verify @component/@integration tags |
| Glue path errors | Use dot notation: com.gymcrm.bdd.component.stepdefs |

## 📚 Documentation Files

- **TESTING_IMPLEMENTATION_GUIDE.md** - Detailed implementation guide
- **IMPLEMENTATION_SUMMARY.md** - Summary of all changes
- **BDD_QUICK_START.md** - Gherkin syntax reference
- **TESTING_FRAMEWORK.md** - Framework overview

## 🎯 Next Steps

1. ✅ Run `mvn clean test-compile` to verify compilation
2. ✅ Run `mvn test` to execute component tests
3. ✅ Run `mvn verify` to execute integration tests
4. 📊 Review test reports in `target/surefire-reports/`
5. 📝 Add new scenarios as needed

## 💡 Best Practices

✅ **DO:**
- Keep scenarios focused on one behavior
- Use descriptive step names
- Mix positive and negative scenarios
- Use DataTable for multiple test cases
- Mock external dependencies in component tests
- Run tests in CI/CD pipeline

❌ **DON'T:**
- Put @SpringBootTest on step classes
- Use @CucumberContextConfiguration in steps
- Hard-code test data values
- Write UI tests as Cucumber tests (use Selenium)
- Skip negative scenarios
- Leave test database dirty

## 📞 Support

See documentation files for:
- **Configuration:** TESTING_IMPLEMENTATION_GUIDE.md
- **Changes:** IMPLEMENTATION_SUMMARY.md
- **Syntax:** BDD_QUICK_START.md
- **Framework:** TESTING_FRAMEWORK.md


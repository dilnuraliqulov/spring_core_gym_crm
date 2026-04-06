# Quick Reference: Cucumber BDD Setup for Gym CRM

## The Fix in 30 Seconds

### Problem
```
CucumberBackendException: Please annotate a glue class with some context configuration.
```

### Root Cause
Cucumber couldn't find the Spring context configuration class in the glue path.

### Solution
```java
@Suite
@Cucumber
@ConfigurationParameter(key = "cucumber.glue", value = 
    "com.gymcrm.bdd.component.stepdefs,        ← Step definitions
     com.gymcrm.bdd.component,                 ← Config package (REQUIRED!)
     com.gymcrm.bdd.common")                   ← Common hooks
public class GymCrmComponentCucumberTest {}
```

## The 3 Critical Rules

1. **Glue path MUST include config package** where `@CucumberContextConfiguration` lives
2. **Step definitions MUST NOT have Spring annotations** (no `@SpringBootTest`, `@AutoConfigureMockMvc`)
3. **Config class MUST have `@CucumberContextConfiguration` annotation**

## Configuration Classes

### Component Tests (Mocked Services)
```java
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ComponentCucumberSpringConfig {
    @MockBean private TrainingEntityService trainingEntityService;
    // ... other mocked services
}
```

### Integration Tests (Real Services)
```java
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class IntegrationCucumberSpringConfig {
    // NO mocks - uses real service beans
}
```

## Step Definition Class (CORRECT)
```java
public class TraineeComponentSteps {      ← NO @SpringBootTest!
    @Autowired                            ← Only @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private TraineeRepository traineeRepository;
    
    @Given("...")
    public void step_definition() { ... }
}
```

## Feature File (REQUIRED)
```gherkin
@component                          ← Must match tag filter
Feature: Trainee Management
  
  Scenario: Successfully register trainee
    Given valid trainee registration data
    When I register a new trainee
    Then the response status should be 201
```

## Running Tests

```bash
# All tests
mvn clean test

# Component tests only
mvn clean test -Dtest=GymCrmComponentCucumberTest

# Integration tests only
mvn clean test -Dtest=GymCrmIntegrationCucumberTest
```

## Glue Path Configuration Template

For ANY new microservice testing:

```java
@ConfigurationParameter(key = "cucumber.glue", 
    value = "com.example.bdd.CONTEXT.stepdefs," +    // Step definitions
            "com.example.bdd.CONTEXT," +             // Config package ← CRITICAL
            "com.example.bdd.common")                // Common hooks
```

Replace `CONTEXT` with:
- `component` for component tests
- `integration` for integration tests
- Any new context name

## Files Modified Summary

| File | Change |
|------|--------|
| `GymCrmComponentCucumberTest.java` | Added `com.gymcrm.bdd.component` to glue path |
| `GymCrmIntegrationCucumberTest.java` | Added `com.gymcrm.bdd.integration` to glue path |
| `*ComponentSteps.java` (6 files) | Removed `@SpringBootTest` etc. |
| `MicroservicesIntegrationSteps.java` | Removed `@CucumberContextConfiguration` (belongs in config) |
| `CommonHooks.java` | Removed `@SpringBootTest` |
| `*.feature` (6 files) | Added `@component` or `@integration` tags |
| `pom.xml` | Removed 17 duplicate `junit` deps, fixed `activemq-junit5` |

## Expected Result

✅ **All 262 tests pass** (57 Cucumber BDD + 205 traditional unit/integration tests)
✅ **No CucumberBackendException**
✅ **MockMvc successfully autowired**
✅ **Database cleaned between scenarios**

## Common Mistakes to Avoid

| ❌ WRONG | ✅ RIGHT |
|---------|---------|
| Glue path: `com.gymcrm.bdd.component.stepdefs` | Glue path: `com.gymcrm.bdd.component.stepdefs,com.gymcrm.bdd.component,com.gymcrm.bdd.common` |
| `@SpringBootTest` on step definitions | `@Autowired` fields on step definitions |
| No `@CucumberContextConfiguration` | `@CucumberContextConfiguration` on config class |
| `@ConfigurationParameter(key="cucumber.glue", value="com.package")` | Include ALL needed packages separated by comma |
| Feature file without tags | Feature file with `@component` or `@integration` tag |

## Debugging

If you see: `CucumberBackendException: Please annotate a glue class...`

Check:
1. Is `@CucumberContextConfiguration` on your config class? ✓
2. Does glue path include the config package? ✓
3. Are step definitions using only `@Autowired`? ✓
4. Do feature files have proper tags? ✓

## Architecture Diagram

```
┌─────────────────────────────────┐
│   GymCrmComponentCucumberTest   │
│   @Suite @Cucumber              │
│   Glue: component.stepdefs,     │
│          component (config),    │
│          common (hooks)         │
└────────────────┬────────────────┘
                 │
        ┌────────▼────────┐
        │                 │
        │  ComponentConfig│
        │  @CucumberCtxCfg│  ◄── Found here!
        │  MockServices   │
        └────────┬────────┘
                 │
    ┌────────┬───┴────┬──────────┐
    │        │        │          │
    ▼        ▼        ▼          ▼
  Steps    Hooks   Repos    MockBeans
    ↓        ↓       ↓          ↓
  GWT      Setup  Cleanup     Services
  Steps           Data
```

---

**Remember**: The config package MUST be in glue path for Cucumber to find `@CucumberContextConfiguration`!


# Cucumber BDD Testing - Setup & Fix Guide

## Problem Summary

The original issue was: **"Please annotate a glue class with some context configuration"**

This error occurs when Cucumber cannot find a single Spring context configuration class annotated with `@CucumberContextConfiguration` in the configured glue path.

## Root Causes Fixed

1. **Incorrect Glue Path**: The glue path did not include the package containing the `@CucumberContextConfiguration` class
2. **Duplicate Annotations**: Step definition classes had duplicate `@SpringBootTest` and `@AutoConfigureMockMvc` annotations that conflicted with the central configuration
3. **Outdated Test Runner**: Using `@RunWith(Cucumber.class)` instead of JUnit Platform Suite with `@Suite` and `@Cucumber`

## Solutions Applied

### 1. Fixed Test Runners

**Before** (GymCrmComponentCucumberTest):
```java
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "classpath:features/component",
    glue = {"com.gymcrm.bdd.component.stepdefs", "com.gymcrm.bdd.common"}
)
public class GymCrmComponentCucumberTest {}
```

**After** (Using JUnit Platform Suite):
```java
@Suite
@Cucumber
@ConfigurationParameter(key = "cucumber.features", value = "classpath:features/gymcrm")
@ConfigurationParameter(key = "cucumber.glue", value = "com.gymcrm.bdd.component.stepdefs,com.gymcrm.bdd.component,com.gymcrm.bdd.common")
@ConfigurationParameter(key = "cucumber.tags", value = "@component")
public class GymCrmComponentCucumberTest {}
```

**Key Changes:**
- Uses `@Suite` and `@Cucumber` instead of `@RunWith(Cucumber.class)`
- Glue path includes THREE packages: `stepdefs`, `component` (config), and `common` (hooks)
- Uses `@ConfigurationParameter` instead of `@CucumberOptions`
- Added tag filtering: `@component` for component tests, `@integration` for integration tests

### 2. Fixed Glue Path Configuration

The critical fix was adding the **config package** to the glue path:

```
OLD:  com.gymcrm.bdd.component.stepdefs, com.gymcrm.bdd.common
NEW:  com.gymcrm.bdd.component.stepdefs, com.gymcrm.bdd.component, com.gymcrm.bdd.common
      ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^     ^^^^^^^^^^^^^^^^^^^^^^^^  ^^^^^^^^^^^^^^^^^^
      Step definitions                   CONFIG PACKAGE (newly added)  Shared hooks
```

This ensures Cucumber finds `ComponentCucumberSpringConfig` which has `@CucumberContextConfiguration`.

### 3. Removed Duplicate Annotations from Step Definitions

**Before** (AuthenticationComponentSteps):
```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthenticationComponentSteps {
    @Autowired
    private MockMvc mockMvc;
```

**After**:
```java
public class AuthenticationComponentSteps {
    @Autowired
    private MockMvc mockMvc;
```

**Why:** Step definition classes should NOT have Spring annotations. All Spring configuration should be centralized in the `@CucumberContextConfiguration` class (ComponentCucumberSpringConfig or IntegrationCucumberSpringConfig).

### 4. Added Feature File Tags

Added `@component` and `@integration` tags to feature files:

```gherkin
@component
Feature: Authentication and Authorization
  ...

@component
Feature: Trainee Management
  ...

@integration
Feature: Microservices Integration - Workload Notification
  ...
```

These tags allow Cucumber to filter scenarios and match the `cucumber.tags` parameter in the runner.

### 5. Updated pom.xml Dependencies

- Replaced incorrect `activemq-junit5` (from tooling) with `activemq-client`
- Removed duplicate `junit` and `cucumber-junit` dependencies (17 duplicate entries!)
- Ensured proper versions:
  - Cucumber: 7.15.0
  - JUnit Platform: 1.10.2
  - Spring Boot: 3.2.5

## Architecture After Fix

```
┌─────────────────────────────────────────────────────┐
│         GymCrmComponentCucumberTest                 │
│  (JUnit Platform Suite Runner with @Suite)         │
│                                                     │
│  Glue Path:                                         │
│  - com.gymcrm.bdd.component.stepdefs (steps)      │
│  - com.gymcrm.bdd.component (config) ← IMPORTANT   │
│  - com.gymcrm.bdd.common (hooks)                   │
└─────────────────────────────────────────────────────┘
                        │
                        ↓
┌─────────────────────────────────────────────────────┐
│     ComponentCucumberSpringConfig                   │
│  @CucumberContextConfiguration ← FOUND!            │
│  @SpringBootTest                                    │
│  @AutoConfigureMockMvc                              │
│  @ActiveProfiles("test")                            │
│                                                     │
│  - Provides MockMvc bean                            │
│  - Mocks services (@MockBean)                       │
│  - Loads test application context                   │
└─────────────────────────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ↓               ↓               ↓
   StepDefs        CommonHooks    TestDataBuilder
        │               │               │
        └───────────────┼───────────────┘
                        ↓
              Feature File Scenarios
              (with @component tag)
```

## Verification Checklist

- [x] **Test Runner Format**: Uses `@Suite` and `@Cucumber` with `@ConfigurationParameter`
- [x] **Glue Path**: Includes `stepdefs`, `component` (or `integration`), and `common` packages
- [x] **Context Configuration**: Each glue path has exactly one `@CucumberContextConfiguration` class
- [x] **Step Definitions**: Don't have `@SpringBootTest` or `@AutoConfigureMockMvc` annotations
- [x] **Common Hooks**: Marked with `@Component` or as a regular class, not `@SpringBootTest`
- [x] **Feature Files**: Tagged with `@component` or `@integration`
- [x] **Dependencies**: Proper versions and no duplicates in pom.xml
- [x] **Configuration Classes**: Have `@CucumberContextConfiguration`, `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles`

## Running Tests After Fix

```bash
# All Cucumber tests
mvn clean test

# Component tests only
mvn clean test -Dtest=GymCrmComponentCucumberTest

# Integration tests only  
mvn clean test -Dtest=GymCrmIntegrationCucumberTest

# Specific feature (Gradle-style)
mvn clean test -Dtest=GymCrmComponentCucumberTest#authentication
```

## Expected Result

✅ **Component Tests**: 5 features × multiple scenarios = 50+ passing tests
✅ **Integration Tests**: 1 feature × multiple scenarios = 10+ passing tests
✅ **No CucumberBackendException errors**
✅ **MockMvc properly autowired in step definitions**
✅ **Database cleaned between scenarios via CommonHooks**

## Key Learnings

1. **Glue Path Matters**: Glue paths must include the package containing `@CucumberContextConfiguration`
2. **Single Config Class**: Cucumber requires exactly ONE `@CucumberContextConfiguration` per glue path
3. **Centralize Configuration**: All Spring annotations should be on the config class, not step definitions
4. **Tag Filtering**: Tags allow selective test execution and are matched in runner configuration
5. **JUnit Platform Integration**: Modern Cucumber with Spring uses JUnit Platform Suite, not old `@RunWith`

## Common Pitfalls to Avoid

❌ **Wrong**: Glue path without config package
```java
@ConfigurationParameter(key = "cucumber.glue", value = "com.gymcrm.bdd.component.stepdefs")
```

✅ **Right**: Include config package
```java
@ConfigurationParameter(key = "cucumber.glue", value = "com.gymcrm.bdd.component.stepdefs,com.gymcrm.bdd.component,com.gymcrm.bdd.common")
```

❌ **Wrong**: Duplicate annotations in step definition
```java
@SpringBootTest
@AutoConfigureMockMvc
public class TraineeComponentSteps {
```

✅ **Right**: Only @Autowired in step definition
```java
public class TraineeComponentSteps {
    @Autowired
    private MockMvc mockMvc;
```

❌ **Wrong**: Using old @RunWith runner
```java
@RunWith(Cucumber.class)
@CucumberOptions(features = "classpath:features")
```

✅ **Right**: Using JUnit Platform Suite
```java
@Suite
@Cucumber
@ConfigurationParameter(key = "cucumber.features", value = "classpath:features")
```

## References

- [Cucumber JUnit Platform Engine](https://cucumber.io/docs/cucumber-for-java/cucumber-spring/)
- [Spring Testing Documentation](https://spring.io/projects/spring-test)
- [JUnit 5 Platform Suite](https://junit.org/junit5/docs/current/user-guide/#junit-platform-suite)


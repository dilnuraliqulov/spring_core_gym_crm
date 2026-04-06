@integration
Feature: Training Integration Tests
  Verifies that creating a training triggers the Training Workload microservice

  Background:
    Given the integration database is clean
    And the following training types are seeded:
      | typeName |
      | Yoga     |
    And a registered trainer with firstName "Coach" and lastName "Smith" and specialization 1
    And a registered trainee with firstName "Student" and lastName "Jones"
    And the trainee is assigned to the trainer

  @integration
  Scenario: Create training successfully
    When I create a training with:
      | field        | value        |
      | trainingName | Morning Yoga |
      | trainingDate | 2026-04-01   |
      | duration     | 60           |
    Then the response status should be 200

  @integration
  Scenario: Create training with non-existent trainer returns 404
    When I call POST "/api/trainings" with invalid trainer and credentials
    Then the response status should be 404

  @integration
  Scenario: Get trainee trainings returns correct list
    Given a training exists for the trainee
    When I request trainings for the trainee
    Then the response status should be 200
    And the response list should not be empty
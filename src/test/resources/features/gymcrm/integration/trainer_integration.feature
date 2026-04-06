@integration
Feature: Trainer Integration Tests

  Background:
    Given the integration database is clean
    And the following training types are seeded:
      | typeName |
      | Yoga     |
      | Fitness  |
      | Pilates  |

  @integration
  Scenario: Register trainer and retrieve profile - full flow
    Given a registered trainer with firstName "John" and lastName "Doe" and specialization 1
    When I call GET "/api/trainers/{trainerUsername}" with saved trainer credentials
    Then the response status should be 200
    And the response body should contain field "firstName" with value "John"

  @integration
  Scenario: Register trainer with invalid specialization returns error
    When I call POST "/api/trainers" with body:
      """
      {
        "firstName": "Jane",
        "lastName": "Doe",
        "specializationId": 9999
      }
      """
    Then the response status should be 404
    And the response error should contain "Training type not found"

  @integration
  Scenario: Get trainer list not assigned to a trainee
    Given a registered trainer with firstName "Trainer" and lastName "One" and specialization 1
    And a registered trainee with firstName "Trainee" and lastName "One"
    When I request unassigned trainers for the trainee
    Then the response status should be 200
    And the response list should contain trainer "Trainer.One"
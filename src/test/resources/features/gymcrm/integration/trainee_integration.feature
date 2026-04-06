@integration
Feature: Trainee Integration Tests
  Tests the full flow of trainee operations through the running application

  Background:
    Given the integration database is clean
    And the following training types are seeded:
      | typeName |
      | Yoga     |
      | Fitness  |

  @integration
  Scenario: Register trainee and retrieve profile - full flow
    When I call POST "/api/trainees" with body:
      """
      {
        "firstName": "Alice",
        "lastName": "Smith",
        "dateOfBirth": "1995-05-15",
        "address": "123 Main St"
      }
      """
    Then the response status should be 201
    And the response should contain "username"
    And the response should contain "password"
    And I save the username from response as "traineeUsername"
    And I save the password from response as "traineePassword"
    When I call GET "/api/trainees/{traineeUsername}" with saved credentials
    Then the response status should be 200
    And the response body should contain field "firstName" with value "Alice"
    And the response body should contain field "lastName" with value "Smith"
    And the response body should contain field "isActive" with value "true"

  @integration
  Scenario: Register trainee with duplicate username generates unique username
    When I call POST "/api/trainees" with body:
      """
      {"firstName": "Bob", "lastName": "Jones", "dateOfBirth": "1990-01-01"}
      """
    Then the response status should be 201
    And I save the username from response as "firstUsername"
    When I call POST "/api/trainees" with body:
      """
      {"firstName": "Bob", "lastName": "Jones", "dateOfBirth": "1992-06-15"}
      """
    Then the response status should be 201
    And I save the username from response as "secondUsername"
    And the saved "secondUsername" should not equal saved "firstUsername"

  @integration
  Scenario: Get profile of non-existent trainee returns 404
    When I call GET "/api/trainees/ghost.user" with credentials "ghost.user" and "wrongpass"
    Then the response status should be 404
    And the response error should contain "User not found"

  @integration
  Scenario: Update trainee profile persists changes
    Given a registered trainee with firstName "Carol" and lastName "White"
    When I update the trainee profile with:
      | field     | value        |
      | firstName | CarolUpdated |
      | lastName  | White        |
      | isActive  | true         |
    Then the response status should be 200
    And the response body should contain field "firstName" with value "CarolUpdated"

  @integration
  Scenario: Delete trainee removes profile permanently
    Given a registered trainee with firstName "Dave" and lastName "Brown"
    When I delete the trainee profile
    Then the response status should be 200
    When I call GET with the deleted trainee credentials
    Then the response status should be 404

  @integration
  Scenario: Activate and deactivate trainee
    Given a registered trainee with firstName "Eve" and lastName "Black"
    When I deactivate the trainee
    Then the response status should be 200
    When I activate the trainee
    Then the response status should be 200
    When I activate the trainee again
    Then the response status should be 400
    And the response error should contain "already active"
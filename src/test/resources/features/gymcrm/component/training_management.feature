@component
Feature: Training management via Gym CRM API
  Trainers and trainees should be able to create training sessions through the API.

  Background:
    Given the database is clean
    And the following training types exist:
      | id | typeName |
      | 1  | Yoga     |
    And trainer "coach" exists with details
      | firstName | lastName | specialization |
      | Coach     | Smith    | Yoga           |
    And a trainee with username "alice" exists
      | firstName | lastName | dateOfBirth | active |
      | Alice     | Doe      | 1995-05-15  | true   |

  Scenario: Successfully add a training session
    Given a valid training add request
    When the client submits the training
    Then the response status should be 200

  Scenario: Reject training due to invalid credentials
    Given a valid training add request
    And authentication credentials are invalid
    When the client submits the training
    Then the response status should be 401
    And the error message should contain "Invalid username or password"

  Scenario: Reject training due to missing request field
    Given an invalid training add request missing "trainingName"
    When the client submits the training
    Then the response status should be 400
    And the error message should contain "Training name is required"
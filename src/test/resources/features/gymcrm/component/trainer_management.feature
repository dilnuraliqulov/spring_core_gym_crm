@component
Feature: Trainer Management
  As a system administrator
  I want to manage trainers in the gym CRM
  So that trainer profiles can be created and retrieved

  Background:
    Given the database is clean
    And the following training types exist:
      | id | typeName |
      | 1  | Yoga     |
      | 2  | Fitness  |
      | 3  | Pilates  |
      | 4  | CrossFit |

  Scenario: Successfully register a new trainer
    Given I have valid trainer registration data
      | firstName | lastName | specialization |
      | John      | Doe      | Yoga           |
    When I register a new trainer
    Then the response status should be 201

  Scenario: Successfully get trainer profile
    Given trainer "trainer1" exists with details
      | firstName | lastName | specialization |
      | John      | Doe      | Yoga           |
    When I request trainer profile for "trainer1"
    Then the response status should be 200

  Scenario: Fail to get profile for non-existent trainer
    When I request trainer profile for "nonexistent"
    Then the response status should be 404
    And the error message should contain "User not found"
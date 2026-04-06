@component
Feature: Training Type Management
  As a system user
  I want to retrieve training types
  So that reference data is available

  Background:
    Given the database is clean
    And the following training types exist:
      | id | typeName |
      | 1  | Yoga     |
      | 2  | Fitness  |
      | 3  | Pilates  |
      | 4  | CrossFit |

  Scenario: Successfully retrieve all training types
    When I request all training types
    Then the response status should be 200
    And the response should contain 4 training types
    And the training types should include "Yoga", "Fitness", "Pilates", "CrossFit"
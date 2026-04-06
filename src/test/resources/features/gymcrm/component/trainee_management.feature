@component
Feature: Trainee Management
  As a system administrator
  I want to manage trainees in the gym CRM
  So that trainee accounts can be created and maintained

  Background:
    Given the database is clean
    And the following training types exist:
      | id | typeName |
      | 1  | Yoga     |
    And trainer "trainer1" exists with details
      | firstName | lastName | specialization |
      | John      | Doe      | Yoga           |

  Scenario: Successfully register a new trainee
    Given I have valid trainee registration data
      | firstName | lastName | dateOfBirth |
      | Alice     | Smith    | 1995-05-15  |
    When I register a new trainee
    Then the response status should be 201

  Scenario: Successfully get trainee profile
    Given a trainee with username "trainee1" exists
      | firstName | lastName | dateOfBirth | active |
      | Bob       | Johnson  | 1998-03-20  | true   |
    When I request trainee profile for "trainee1"
    Then the response status should be 200
    And the trainee profile should contain correct information

  Scenario: Fail to get profile for non-existent trainee
    When I request trainee profile for "nonexistent"
    Then the response status should be 404
    And the error message should contain "User not found"
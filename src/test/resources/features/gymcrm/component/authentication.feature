@component
Feature: Authentication
  As a system user
  I want to authenticate in the system
  So that I can access secured APIs

  Background:
    Given the database is clean
    And the following users exist:
      | username | password    | firstName | lastName | active |
      | admin    | admin1234   | Admin     | User     | true   |
      | trainer1 | password123 | John      | Doe      | true   |
      | trainee1 | password123 | Alice     | Smith    | true   |
      | inactive | password123 | Inactive  | User     | false  |

  Scenario: Successfully authenticate with valid credentials
    When I authenticate with username "trainer1" and password "password123"
    Then the response status should be 200
    And the authentication token should be generated

  Scenario: Fail to authenticate with invalid password
    When I authenticate with username "trainer1" and password "wrongpassword"
    Then the response status should be 401
    And the error message should contain "Invalid credentials"

  Scenario: Fail to authenticate with non-existent user
    When I authenticate with username "nonexistent" and password "password123"
    Then the response status should be 401
    And the error message should contain "Invalid credentials"

  Scenario: Fail to authenticate inactive user
    When I authenticate with username "inactive" and password "password123"
    Then the response status should be 401
    And the error message should contain "Invalid credentials"
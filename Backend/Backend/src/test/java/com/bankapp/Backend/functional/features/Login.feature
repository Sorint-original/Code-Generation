Feature: User Login

  Scenario: Successful login
    Given a valid user exists with email "alice@example.com" and password "password123"
    When I send a login request with email "alice@example.com" and password "password123"
    Then I should receive a 200 OK response in login
    And the response should contain a JWT token

Feature: Transfer funds between customer accounts

  Scenario: Employee successfully transfers funds between two accounts
    Given I am logged in as an employee for transferring funds
    When I send a POST request to the transfer endpoint with valid data
    Then I should receive a 200 OK response confirming the transfer

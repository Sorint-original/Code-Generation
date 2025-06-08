Feature: Employee blocks a bank account

  Scenario: Successfully block an active account
    Given I am logged in as an employee to block accounts
    When I send a PUT request to block account with IBAN
    Then I should receive a 200 OK response confirming account is blocked
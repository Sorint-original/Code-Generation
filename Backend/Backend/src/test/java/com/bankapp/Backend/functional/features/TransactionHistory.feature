Feature: Retrieve transaction history

  Scenario: Successfully retrieve logged-in customer's transaction history
    Given I am logged in as a customer to view transaction history
    When I send a GET request to the transaction history endpoint
    Then I should receive a 200 OK response with transaction history
    And The response should contain at least one transaction with my account IBAN

  Scenario: Successfully retrieve all transactions as employee
    Given I am logged in as an employee to view all transactions
    When I send a GET request to the all transactions endpoint
    Then I should receive a 200 OK response with all transactions
    And The response should contain transactions from multiple accounts


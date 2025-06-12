Feature: ATM Transactions

  Scenario: Successful ATM withdrawal
    Given a valid user is logged in with email "alice@example.com" and password "password123"
    And a bank account is fetched for the logged-in user
    When the user sends a withdrawal request for 100.00
    Then the response status should be 200 OK
    And the response should contain "Withdrawal successful"

  Scenario: Successful ATM deposit
    Given a valid user is logged in with email "alice@example.com" and password "password123"
    And a bank account is fetched for the logged-in user
    When the user sends a deposit request for 150.00
    Then the response status should be 200 OK
    And the response should contain "Deposit successful"
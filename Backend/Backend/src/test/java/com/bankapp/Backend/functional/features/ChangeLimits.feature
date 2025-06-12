Feature: Employee changes customer's transfer limits

  Scenario: Employee updates daily and absolute limits for a customer's account
    Given I am logged in as an employee with email "bob@example.com" and password "secureaccess"
    When I fetch all bank accounts
    And I change the daily limit to 2000.00 and the absolute limit to 5.00 for the first account
    Then the API response should be 200
    And the message should contain "limits changes successfully"

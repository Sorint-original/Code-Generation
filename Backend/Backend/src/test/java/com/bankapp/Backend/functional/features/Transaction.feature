Feature: Customer transfers funds to another customer

  Scenario: Customer searches for recipient and transfers money
    Given I am logged in as a customer with email "alice@example.com" and password "password123"
    When I search for recipients with the name "bob"
    And I save the IBAN of the first recipient of type "CHECKING"
    And I save my own checking account IBAN
    And I initiate a transfer of amount 50.00
    Then The transaction should succeed with status 200
    And the response message should be "Transaction transferred successfully"

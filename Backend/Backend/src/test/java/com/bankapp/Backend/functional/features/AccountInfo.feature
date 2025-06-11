Feature: Retrieve customer account information

  Scenario: Successfully retrieve logged-in customer's account info
    Given I am logged in as a customer to view account info
    When I send a GET request to the account info endpoint
    Then I should receive a 200 OK response with customer account info
    And The response should contain customer name and at least one account

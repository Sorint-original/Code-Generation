Feature: Approve or unapprove customers

  Scenario: Approve a customer
    Given I am logged in as an employee
    And there is a customer with status "UNAPPROVED"
    When I send a POST request to "approve" the customer
    Then the customer's status should be "Approved"

  Scenario: Unapprove a customer
    Given I am logged in as an employee
    And there is a customer with status "APPROVED"
    When I send a POST request to "decline" the customer
    Then the customer's status should be "Denied"

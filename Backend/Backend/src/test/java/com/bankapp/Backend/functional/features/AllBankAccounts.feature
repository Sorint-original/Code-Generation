Feature: Manage bank accounts

  Scenario: Retrieve all customer bank accounts
    Given I have the role "EMPLOYEE"
    When I send a GET request to "/employee/account/all"
    Then I should receive a 200 OK response in showing all accounts

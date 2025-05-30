Feature: Manage unapproved customers

  Scenario: Retrieve all unapproved customers
    Given I have the role "EMPLOYEE"
    When I send a GET request to "/api/employee/unapproved-customers"
    Then I should receive a 200 OK response in showing un approved customer
    And the response should include at least one unapproved customer

@Before
Feature: Customer Registration

  Scenario: Successful customer registration
    Given a valid customer registration request
    When I submit the registration request
    Then I should receive a 201 Created response

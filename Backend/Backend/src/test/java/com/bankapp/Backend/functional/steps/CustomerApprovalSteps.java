package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class CustomerApprovalSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private String employeeToken;
    private Long customerId;
    private HttpStatus latestStatus;

    @Given("I am logged in as an employee")
    public void i_am_logged_in_as_employee() throws JSONException {
        String email = "bob@example.com";
        String password = "secureaccess";

        String loginPayload = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:8080/api/login",
                new HttpEntity<>(loginPayload, headers),
                String.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        employeeToken = new JSONObject(loginResponse.getBody()).getString("token");
    }

    @And("there is a customer with status {string}")
    public void there_is_a_customer_with_status(String status) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String endpoint = "http://localhost:8080/api/employee/unapproved-customers";
        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        JSONArray users = new JSONArray(response.getBody());
        assertTrue(users.length() > 0, "No users with status " + status + " found");

        JSONObject user = users.getJSONObject(0);
        customerId = user.getLong("id");
        System.out.println("customerId: " + customerId);
    }

    @When("I send a POST request to {string} the customer")
    public void i_send_a_post_request_to(String action) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String endpoint = "http://localhost:8080/api/employee/customers/" + customerId + "/" + action;

        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
        latestStatus = (HttpStatus) response.getStatusCode();
    }

    @Then("the customer's status should be {string}")
    public void the_customer_status_should_be(String expectedStatus) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String endpoint = "http://localhost:8080/api/employee/user/" + customerId;

        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.GET, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        JSONObject user = new JSONObject(response.getBody());
        String actualStatus = user.getString("status");
        System.out.println("expected: " + expectedStatus + " actual: " + actualStatus);

        assertEquals(expectedStatus, actualStatus);
    }
}

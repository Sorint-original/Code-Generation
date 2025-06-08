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
        System.out.println("Selected customerId: " + customerId);
    }

    @When("I send a POST request to {string} the customer")
    public void i_send_a_post_request_to(String action) throws JSONException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String endpoint;
        HttpEntity<?> requestEntity;

        if (action.equalsIgnoreCase("approve")) {
            // Send JSON body with limits
            JSONObject requestBody = new JSONObject();
            requestBody.put("customerId", customerId);
            requestBody.put("absoluteTransferLimit", 10000);
            requestBody.put("dailyTransferLimit", 5000);

            requestEntity = new HttpEntity<>(requestBody.toString(), headers);
            endpoint = "http://localhost:8080/api/employee/customers/approve";

        } else if (action.equalsIgnoreCase("decline")) {
            // Send only headers; ID is part of the path
            requestEntity = new HttpEntity<>(headers);
            endpoint = "http://localhost:8080/api/employee/customers/" + customerId + "/decline";

        } else {
            throw new IllegalArgumentException("Unsupported action: " + action);
        }

        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, requestEntity, String.class);
        latestStatus = (HttpStatus) response.getStatusCode();
        assertTrue(latestStatus.is2xxSuccessful(), "Expected 2xx status, got: " + latestStatus);
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

        System.out.println("Expected: " + expectedStatus + " | Actual: " + actualStatus);
        assertEquals(expectedStatus, actualStatus);
    }
}

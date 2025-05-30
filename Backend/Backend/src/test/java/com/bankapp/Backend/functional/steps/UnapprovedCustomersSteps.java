package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class UnapprovedCustomersSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String employeeToken;

    @Given("I have the role {string}")
    public void i_have_the_role(String role) throws JSONException {
        // You can hardcode or login and get a token from a known employee
        // Here we assume you've already seeded an employee account
        String email = "bob@example.com";
        String password = "secureaccess";

        String loginUrl = "http://localhost:8080/api/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String loginPayload = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        System.out.println("Login response body: " + loginResponse.getBody());

        employeeToken = new org.json.JSONObject(loginResponse.getBody()).getString("token");
    }

    @When("I send a GET request to {string}")
    public void i_send_a_get_request(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        response = restTemplate.exchange("http://localhost:8080" + path, HttpMethod.GET, entity, String.class);
    }

    @Then("I should receive a 200 OK response in showing un approved customer")
    public void i_should_receive_a_200_ok_response() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("the response should include at least one unapproved customer")
    public void the_response_should_include_unapproved_customers() throws JSONException {
        JSONArray body = new JSONArray(response.getBody());
        assertTrue(body.length() >= 1);
    }
}

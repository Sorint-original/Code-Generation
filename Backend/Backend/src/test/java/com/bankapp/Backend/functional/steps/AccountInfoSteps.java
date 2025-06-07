package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class AccountInfoSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String customerToken;

    @Given("I am logged in as a customer to view account info")
    public void i_am_logged_in_as_a_customer_to_view_account_info() throws Exception {
        String email = "alice@example.com";
        String password = "password123";

        String loginUrl = "http://localhost:8080/api/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String loginPayload = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        JSONObject body = new JSONObject(loginResponse.getBody());
        customerToken = body.getString("token");
    }

    @When("I send a GET request to the account info endpoint")
    public void i_send_a_get_request_to_the_account_info_endpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        response = restTemplate.exchange(
                "http://localhost:8080/api/account/info",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    @Then("I should receive a 200 OK response with customer account info")
    public void i_should_receive_a_200_ok_response_with_customer_account_info() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("The response should contain customer name and at least one account")
    public void the_response_should_contain_customer_name_and_at_least_one_account() throws Exception {
        JSONObject body = new JSONObject(response.getBody());
        assertTrue(body.has("firstName"));
        assertTrue(body.has("lastName"));
        assertTrue(body.has("bankAccounts"));
        assertTrue(body.getJSONArray("bankAccounts").length() >= 1);
    }
}


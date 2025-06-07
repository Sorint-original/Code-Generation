package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class BlockAccountSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String employeeToken;
    private String testIban = "2134564321";

    @Given("I am logged in as an employee to block accounts")
    public void i_am_logged_in_as_an_employee_to_block_accounts() throws Exception {
        String email = "bob@example.com";
        String password = "secureaccess";

        String loginUrl = "http://localhost:8080/api/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String loginPayload = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        JSONObject body = new JSONObject(loginResponse.getBody());
        employeeToken = body.getString("token");
    }

    @When("I send a PUT request to block account with IBAN")
    public void i_send_a_put_request_to_block_account_with_iban() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        String url = "http://localhost:8080/api/employee/account/close/" + testIban;
        response = restTemplate.exchange(url, HttpMethod.PUT, entity, String.class);
    }

    @Then("I should receive a 200 OK response confirming account is blocked")
    public void i_should_receive_a_200_ok_response_confirming_account_is_blocked() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}


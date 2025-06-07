package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class AllBankAccountsSteps {
    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String employeeToken;

    @Given("I have the EMPLOYEE role")
    public void i_have_the_employee_role() throws JSONException {
        String email = "bob@example.com";
        String password = "secureaccess";

        String loginUrl = "http://localhost:8080/api/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String loginPayload = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        employeeToken = new JSONObject(loginResponse.getBody()).getString("token");
    }

    @When("I send a GET request to {string} to retrieve all accounts")
    public void i_send_a_get_request_to_retrieve_all_accounts(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        response = restTemplate.exchange("http://localhost:8080" + path, HttpMethod.GET, entity, String.class);
    }

    @Then("I should receive a 200 OK response with all accounts")
    public void i_should_receive_a_200_ok_response_with_all_accounts() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

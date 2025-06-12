package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionLimitSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String employeeToken;
    private String firstIban;

    @Given("I am logged in as an employee with email {string} and password {string}")
    public void login_as_employee(String email, String password) throws Exception {
        String loginPayload = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, password);

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

    @When("I fetch all bank accounts")
    public void fetch_all_bank_accounts() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        response = restTemplate.exchange(
                "http://localhost:8080/api/employee/account/all",
                HttpMethod.GET,
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONArray accounts = new JSONArray(response.getBody());

        assertTrue(accounts.length() > 0);
        firstIban = accounts.getJSONObject(0).getString("iban");
        assertNotNull(firstIban);
    }

    @And("I change the daily limit to {double} and the absolute limit to {double} for the first account")
    public void change_limits_for_first_account(Double daily, Double absolute) {
        String url = "http://localhost:8080/api/employee/change-limit";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(employeeToken);

        String payload = String.format("""
            {
              \"iban\": \"%s\",
              \"dailyLimit\": %f,
              \"absoluteLimit\": %f
            }
            """, firstIban, daily, absolute);

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, entity, String.class);
    }

    @Then("the API response should be {int}")
    public void assert_status_code(int statusCode) {
        assertEquals(statusCode, response.getStatusCodeValue());
    }

    @And("the message should contain {string}")
    public void assert_response_message(String message) {
        assertTrue(response.getBody().contains(message));
    }
}

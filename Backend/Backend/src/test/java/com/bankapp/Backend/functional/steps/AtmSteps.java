package com.bankapp.Backend.functional.steps;

import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.DTO.LoginRequest;
import io.cucumber.java.en.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class AtmSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private String token;
    private ResponseEntity<String> response;
    private String iban;

    @Given("a valid user is logged in with email {string} and password {string}")
    public void a_valid_user_is_logged_in(String email, String password) throws JSONException {
        LoginRequest request = new LoginRequest(email, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity("http://localhost:8080/api/login", entity, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());

        JSONObject body = new JSONObject(loginResponse.getBody());
        token = body.getString("token");
    }

    @And("a bank account is fetched for the logged-in user")
    public void a_bank_account_is_fetched_for_the_logged_in_user() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "http://localhost:8080/api/account/info",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            assertEquals(HttpStatus.OK, response.getStatusCode());

            String responseBody = response.getBody();
            assertNotNull(responseBody);

            JSONObject json = new JSONObject(responseBody);
            var accounts = json.getJSONArray("bankAccounts");

            assertTrue(accounts.length() > 0, "❌ No accounts found for the user.");

            JSONObject firstAccount = accounts.getJSONObject(0);
            this.iban = firstAccount.getString("iban");
            double balance = firstAccount.getDouble("amount");

            System.out.println("✅ Fetched IBAN: " + iban);
            System.out.println("✅ Fetched Balance: " + balance);

        } catch (Exception e) {
            fail("❌ Failed to fetch account info: " + e.getMessage());
        }
    }


    @When("the user sends a withdrawal request for {double}")
    public void the_user_sends_a_withdrawal_request(Double amount) {
        sendAtmRequest("/api/atm/withdraw", amount);
    }

    @When("the user sends a deposit request for {double}")
    public void the_user_sends_a_deposit_request(Double amount) {
        sendAtmRequest("/api/atm/deposit", amount);
    }

    private void sendAtmRequest(String endpoint, Double amount) {
        AtmRequest request = new AtmRequest(iban, new java.math.BigDecimal(amount));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<AtmRequest> entity = new HttpEntity<>(request, headers);
        response = restTemplate.postForEntity("http://localhost:8080" + endpoint, entity, String.class);
    }

    @Then("the response status should be 200 OK")
    public void the_response_status_should_be_200_ok() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("the response should contain {string}")
    public void the_response_should_contain(String expectedText) {
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains(expectedText));
    }
}

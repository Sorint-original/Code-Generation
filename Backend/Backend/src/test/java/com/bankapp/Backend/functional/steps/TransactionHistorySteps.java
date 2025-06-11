package com.bankapp.Backend.functional.steps;

import com.bankapp.Backend.DTO.LoginRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.cucumber.java.en.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionHistorySteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String authToken;
    private String userEmail;

    // Login pattern from LoginSteps
    @Given("I am logged in as {string} with password {string} to access transaction history")
    public void i_am_logged_in_as(String email, String password) throws JSONException {
        this.userEmail = email;
        LoginRequest request = new LoginRequest(email, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "http://localhost:8080/api/login",
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        this.authToken = new JSONObject(loginResponse.getBody()).getString("token");
    }

    // Endpoint request pattern from AccountInfoSteps
    @When("I request my transaction history")
    public void i_request_my_transaction_history() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        response = restTemplate.exchange(
                "http://localhost:8080/api/transactionHistory/fetchLoggedUserTransactions",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    @When("I request all transactions as an employee")
    public void i_request_all_transactions_as_an_employee() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        response = restTemplate.exchange(
                "http://localhost:8080/api/transactionHistory/fetchAllTransactions",
                HttpMethod.GET,
                entity,
                String.class
        );
    }

    // Response validation pattern from both examples
    @Then("the transaction history request should succeed")
    public void the_transaction_history_request_should_succeed() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("the response should contain transactions for my accounts")
    public void the_response_should_contain_transactions_for_my_accounts() throws JSONException {
        JSONArray transactions = new JSONArray(response.getBody());
        assertTrue(transactions.length() > 0);

        // In a real implementation, fetch user's IBANs first
        String expectedIbanPrefix = userEmail.equals("employee@bank.com") ? "EMP" : "CUST";

        boolean hasUserTransactions = false;
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject txn = transactions.getJSONObject(i);
            if (txn.getString("fromIban").startsWith(expectedIbanPrefix) ||
                    txn.getString("toIban").startsWith(expectedIbanPrefix)) {
                hasUserTransactions = true;
                break;
            }
        }
        assertTrue(hasUserTransactions);
    }

    @And("the response should contain transactions from multiple customers")
    public void the_response_should_contain_transactions_from_multiple_customers() throws JSONException {
        JSONArray transactions = new JSONArray(response.getBody());
        assertTrue(transactions.length() > 1);

        String firstCustomerId = transactions.getJSONObject(0).getString("initiatorId");
        boolean hasMultipleCustomers = false;

        for (int i = 1; i < transactions.length(); i++) {
            if (!transactions.getJSONObject(i).getString("initiatorId").equals(firstCustomerId)) {
                hasMultipleCustomers = true;
                break;
            }
        }
        assertTrue(hasMultipleCustomers);
    }
}
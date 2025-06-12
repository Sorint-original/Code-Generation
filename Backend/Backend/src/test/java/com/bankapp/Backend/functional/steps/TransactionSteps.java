package com.bankapp.Backend.functional.steps;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String customerToken;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String recipientIban;
    private String checkingIban;

    @Given("I am logged in as a customer with email {string} and password {string}")
    public void i_am_logged_in_as_customer(String email, String password) throws Exception {
        String loginUrl = "http://localhost:8080/api/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String loginPayload = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(loginPayload, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(loginUrl, entity, String.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        customerToken = new JSONObject(loginResponse.getBody()).getString("token");
    }

    @When("I search for recipients with the name {string}")
    public void i_search_for_recipients(String name) {
        String url = "http://localhost:8080/api/customer/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = new HashMap<>();
        body.put("query", name);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

        response = restTemplate.postForEntity(url, entity, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("I save the IBAN of the first recipient of type {string}")
    public void i_save_iban_of_type(String type) throws Exception {
        JSONArray arr = new JSONArray(response.getBody());
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("accountType").equalsIgnoreCase(type)) {
                recipientIban = obj.getString("iban");
                break;
            }
        }
        assertNotNull(recipientIban);
    }

    @And("I save my own checking account IBAN")
    public void i_save_own_checking_iban() throws Exception {
        String url = "http://localhost:8080/api/account/info";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> accResponse = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        System.out.println(accResponse.getBody());
        assertEquals(HttpStatus.OK, accResponse.getStatusCode());

        JsonNode accounts = objectMapper.readTree(accResponse.getBody()).get("bankAccounts");
        for (JsonNode account : accounts) {
            if (account.get("type").asText().equals("CHECKING")) {
                checkingIban = account.get("iban").asText();
                break;
            }
        }
        assertNotNull(checkingIban);
    }

    @And("I initiate a transfer of amount {double}")
    public void i_initiate_transfer(Double amount) {
        String url = "http://localhost:8080/api/transaction";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(customerToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> payload = new HashMap<>();
        payload.put("fromAccountIban", checkingIban);
        payload.put("toAccountIban", recipientIban);
        payload.put("amount", BigDecimal.valueOf(amount));
        payload.put("initiatorEmail", "alice@example.com");
        payload.put("accountType", "CHECKING");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        response = restTemplate.postForEntity(url, entity, String.class);
    }

    @Then("The transaction should succeed with status {int}")
    public void transaction_should_succeed(int status) {
        assertEquals(status, response.getStatusCodeValue());
    }

    @And("the response message should be {string}")
    public void response_should_contain_success_message(String expectedMessage) {
        assertTrue(response.getBody().contains(expectedMessage));
    }

}

package com.bankapp.Backend.functional.steps;

import io.cucumber.java.en.*;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class TransferFundsEmployeeSteps {
    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String employeeToken;

    private final String transferUrl = "http://localhost:8080/api/employee/transfer";

    @Given("I am logged in as an employee for transferring funds")
    public void i_am_logged_in_as_an_employee_for_transferring_funds() throws Exception {
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

    @When("I send a POST request to the transfer endpoint with valid data")
    public void i_send_a_post_request_to_the_transfer_endpoint_with_valid_data() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(employeeToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String transferPayload = """
        {
          "fromAccountIban": "21345643211",
          "toAccountIban": "2134564321",
          "amount": 100.00,
          "initiatorEmail": "bob@example.com"
        }
        """;

        HttpEntity<String> entity = new HttpEntity<>(transferPayload, headers);
        response = restTemplate.postForEntity(transferUrl, entity, String.class);
    }

    @Then("I should receive a 200 OK response confirming the transfer")
    public void i_should_receive_a_200_ok_response_confirming_the_transfer() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

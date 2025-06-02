package com.bankapp.Backend.functional.steps;

import com.bankapp.Backend.DTO.LoginRequest;
import org.json.JSONException;
import org.json.JSONObject;
import io.cucumber.java.en.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

public class LoginSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private ResponseEntity<String> response;
    private String email;
    private String password;

    @Given("a valid user exists with email {string} and password {string}")
    public void a_valid_user_exists(String email, String password) {
        this.email = email;
        this.password = password;

        // You can either mock this or ensure this user exists before running the test
        // Assume the user is already registered for now
    }

    @When("I send a login request with email {string} and password {string}")
    public void i_send_a_login_request(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(request, headers);
        response = restTemplate.postForEntity("http://localhost:8080/api/login", entity, String.class);
    }

    @Then("I should receive a 200 OK response in login")
    public void i_should_receive_a_200_ok_response() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("the response should contain a JWT token")
    public void the_response_should_contain_a_jwt_token() throws JSONException {
        JSONObject body = new JSONObject(response.getBody());
        assertTrue(body.has("token"));
        assertFalse(body.getString("token").isEmpty());
    }
}

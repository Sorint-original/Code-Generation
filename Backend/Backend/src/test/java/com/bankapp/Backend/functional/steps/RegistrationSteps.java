package com.bankapp.Backend.functional.steps;

import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;


public class RegistrationSteps {

    private final RestTemplate restTemplate = new RestTemplate();
    private CustomerRegistrationRequest request;
    private ResponseEntity<String> response;

    @Before
    public void setup() {
        System.out.println(">>> Starting registration test scenario");
    }

    @Given("a valid customer registration request")
    public void a_valid_customer_registration_request() {
        request = new CustomerRegistrationRequest();
        request.setFirstName("Ziad");
        request.setLastName("Alatrash");
        request.setUserName("ziad123");
        request.setEmail("ziad@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("0638968768");
        request.setBsnNumber("654323456654");
    }

    @When("I submit the registration request")
    public void i_submit_the_registration_request() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CustomerRegistrationRequest> entity = new HttpEntity<>(request, headers);

        response = restTemplate.postForEntity("http://localhost:8080/api/user/register", entity, String.class);
    }

    @Then("I should receive a 201 Created response")
    public void i_should_receive_a_201_created_response() {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
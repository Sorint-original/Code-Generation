package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import com.bankapp.Backend.DTO.CustomerRegistrationResponse;
import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerRegistrationControllerTest {

    private final UserService userService = mock(UserService.class);
    private final CustomerRegistrationController controller = new CustomerRegistrationController(userService);

    @Test
    void registerCustomer_ShouldReturnCreatedResponse() {
        // Arrange
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setPhoneNumber("1234567890");
        request.setBsnNumber("123456789");
        request.setUserName("john_doe");

        User mockUser = new User();
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john@example.com");
        mockUser.setRole(Role.CUSTOMER);
        mockUser.setStatus(CustomerStatus.Pending);

        CustomerRegistrationResponse mockResponse = new CustomerRegistrationResponse(
                mockUser, true, "Registration successful. Your account is pending approval.");

        when(userService.registerCustomer(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CustomerRegistrationResponse> response = controller.registerCustomer(request);

        // Assert
        assertEquals(201, response.getStatusCodeValue());
        assertEquals("Registration successful. Your account is pending approval.", response.getBody().getMessage());
        assertTrue(response.getBody().isSuccess());

        verify(userService, times(1)).registerCustomer(request);
    }
}
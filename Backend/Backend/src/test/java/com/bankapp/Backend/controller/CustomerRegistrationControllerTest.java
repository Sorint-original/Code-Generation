package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import com.bankapp.Backend.DTO.CustomerRegistrationResponse;
import com.bankapp.Backend.exception.DuplicateFieldException;
import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.UserService;
import org.junit.jupiter.api.Test;
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
    @Test
    void registerCustomer_ShouldThrowDuplicateEmailException() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setEmail("duplicate@example.com");
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setPassword("password123");
        request.setPhoneNumber("0612345678");
        request.setBsnNumber("987654321");
        request.setUserName("jane_doe");

        when(userService.registerCustomer(any())).thenThrow(
                new DuplicateFieldException("Email", "duplicate@example.com")
        );

        DuplicateFieldException exception = assertThrows(DuplicateFieldException.class, () ->
                controller.registerCustomer(request)
        );

        assertEquals("Email '"+ request.getEmail() + "' is already in use.", exception.getMessage());
        verify(userService).registerCustomer(request);
    }
    @Test
    void registerCustomer_ShouldThrowDuplicatePhoneNumberException() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setEmail("unique@example.com");
        request.setPhoneNumber("0612345678");
        request.setFirstName("Alex");
        request.setLastName("Smith");
        request.setPassword("strongpass");
        request.setBsnNumber("112233445");
        request.setUserName("alex_smith");

        when(userService.registerCustomer(any())).thenThrow(
                new DuplicateFieldException("Phone number", "0612345678")
        );

        DuplicateFieldException exception = assertThrows(DuplicateFieldException.class, () ->
                controller.registerCustomer(request)
        );

        assertEquals("Phone number '" + request.getPhoneNumber().toString() + "' is already in use.", exception.getMessage());
        verify(userService).registerCustomer(request);
    }
    @Test
    void registerCustomer_ShouldThrowDuplicateBsnNumberException() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setEmail("bsn@example.com");
        request.setPhoneNumber("0687654321");
        request.setFirstName("Sam");
        request.setLastName("Taylor");
        request.setPassword("safePassword");
        request.setBsnNumber("999888777");
        request.setUserName("sam_t");

        when(userService.registerCustomer(any())).thenThrow(
                new DuplicateFieldException("BSN number", "999888777")
        );

        DuplicateFieldException exception = assertThrows(DuplicateFieldException.class, () ->
                controller.registerCustomer(request)
        );

        assertEquals("BSN number '" + request.getBsnNumber() +"' is already in use.", exception.getMessage());
        verify(userService).registerCustomer(request);
    }



}
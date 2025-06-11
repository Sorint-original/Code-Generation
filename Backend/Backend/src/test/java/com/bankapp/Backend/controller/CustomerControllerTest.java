package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.CustomerIbanRequest;
import com.bankapp.Backend.DTO.CustomerIbanResponse;
import com.bankapp.Backend.DTO.DashboardStatusResponse;
import com.bankapp.Backend.DTO.RecipientAccount;
import com.bankapp.Backend.service.CustomerService;
import com.bankapp.Backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomerController customerController;

    // ───────────── getIbansByCustomerName ─────────────

    @Test
    void testGetIbansByCustomerName_success() {
        CustomerIbanRequest request = new CustomerIbanRequest("John", "Doe");
        List<CustomerIbanResponse> mockResponse = List.of(
                new CustomerIbanResponse("NL01BANK1234567890", "CHECKING")
        );

        when(customerService.getIbansByName(request)).thenReturn(mockResponse);

        ResponseEntity<List<CustomerIbanResponse>> response = customerController.getIbansByCustomerName(request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(customerService, times(1)).getIbansByName(request);
    }

    @Test
    void testGetIbansByCustomerName_whenServiceThrows_shouldPropagate() {
        CustomerIbanRequest request = new CustomerIbanRequest("Not", "Found");

        when(customerService.getIbansByName(request)).thenThrow(new IllegalArgumentException("Customer not found"));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> customerController.getIbansByCustomerName(request));

        assertEquals("Customer not found", ex.getMessage());
    }

    // ───────────── searchRecipients ─────────────

    @Test
    void testSearchRecipients_success() {
        String query = "Jane";
        List<RecipientAccount> mockResults = List.of(
                new RecipientAccount("Jane Smith", "NL02BANK0987654321", "CHECKING")
        );

        when(customerService.searchRecipientAccounts(query)).thenReturn(mockResults);

        ResponseEntity<List<RecipientAccount>> response = customerController.searchRecipients(Map.of("query", query));

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResults, response.getBody());
        verify(customerService, times(1)).searchRecipientAccounts(query);
    }

    @Test
    void testSearchRecipients_whenQueryMissing_shouldThrowIllegalArgument() {
        Map<String, String> badInput = Map.of(); // no "query" key

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> customerController.searchRecipients(badInput));

        assertEquals("Missing 'query' field in request body.", ex.getMessage());
    }


    @Test
    void testSearchRecipients_whenServiceThrows_shouldPropagate() {
        String query = "z";
        when(customerService.searchRecipientAccounts(query))
                .thenThrow(new IllegalArgumentException("Query too short"));

        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> customerController.searchRecipients(Map.of("query", query)));

        assertEquals("Query too short", ex.getMessage());
    }

    // ───────────── getCustomerDashboardStatus ─────────────

    @Test
    void testGetCustomerDashboardStatus_success() {
        DashboardStatusResponse expected = new DashboardStatusResponse("Welcome to your dashboard!");
        when(userService.getCustomerDashboardStatus()).thenReturn(expected);

        ResponseEntity<DashboardStatusResponse> response = customerController.getCustomerDashboardStatus();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(expected, response.getBody());
        verify(userService, times(1)).getCustomerDashboardStatus();
    }

    @Test
    void testGetCustomerDashboardStatus_whenServiceThrows_shouldPropagate() {
        when(userService.getCustomerDashboardStatus()).thenThrow(new RuntimeException("Unexpected failure"));

        Exception ex = assertThrows(RuntimeException.class,
                () -> customerController.getCustomerDashboardStatus());

        assertEquals("Unexpected failure", ex.getMessage());
    }
}

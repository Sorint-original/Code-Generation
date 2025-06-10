package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.ApproveCustomerRequest;
import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.DTO.GlobalApiResponse;
import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.exception.UserNotFoundException;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.service.BankAccountService;
import com.bankapp.Backend.service.EmployeeService;
import com.bankapp.Backend.service.TransactionService;
import com.bankapp.Backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private UserService userService;

    @Mock
    private EmployeeService employeeService;


    @Mock
    private TransactionService transactionService;

    private BankAccount testAccount;
    private BankAccountResponse testResponse;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1L);

        testAccount = new BankAccount();
        testAccount.setId(100L);
        testAccount.setUser(user);
        testAccount.setAmount(BigDecimal.valueOf(500));
        testAccount.setType(AccountType.CHECKING);
        testAccount.setIban("NL01TEST0123456789");
        testAccount.setAbsoluteTransferLimit(BigDecimal.valueOf(1000));
        testAccount.setDailyTransferLimit(BigDecimal.valueOf(500));
        testAccount.setStatus(AccountStatus.APPROVED);

        testResponse = new BankAccountResponse(
                testAccount.getId(),
                user.getId(),
                testAccount.getAmount(),
                testAccount.getAccountType(),
                testAccount.getIban(),
                testAccount.getAbsoluteTransferLimit(),
                testAccount.getDailyTransferLimit(),
                testAccount.getStatus()
        );
    }

    @Test
    void getAllBankAccounts_ShouldReturnListOfBankAccountResponses() {
        List<BankAccount> mockAccounts = List.of(testAccount);
        List<BankAccountResponse> mockResponses = List.of(testResponse);

        when(bankAccountService.getAllBankAccounts()).thenReturn(mockAccounts);
        when(bankAccountService.accountsToResponses(mockAccounts)).thenReturn(mockResponses);

        ResponseEntity<List<BankAccountResponse>> result = employeeController.getAllBankAccounts();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(mockResponses, result.getBody());
        verify(bankAccountService).getAllBankAccounts();
        verify(bankAccountService).accountsToResponses(mockAccounts);
    }

    @Test
    void updateAccountStatus_ShouldUpdateStatusToBlocked() {
        String iban = "NL01TEST0123456789";

        ResponseEntity<Void> result = employeeController.updateAccountStatus(iban);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(bankAccountService).updateAccountStatus(iban, AccountStatus.BLOCKED);
    }

    @Test
    void transferFunds_ShouldCallTransferFundsEmployee() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(100));
        request.setInitiatorEmail("test@mail.com");

        ResponseEntity<Void> result = employeeController.transferFunds(request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(transactionService).transferFundsEmployee(request);
    }

    @Test
    void getUnapprovedCustomers_shouldReturnListOfUsers() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setStatus(CustomerStatus.Pending);

        when(userService.findUnapprovedUsers(Role.CUSTOMER))
                .thenReturn(List.of(mockUser));

        ResponseEntity<List<User>> response = employeeController.getUnapprovedCustomers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        assertEquals(CustomerStatus.Pending, response.getBody().get(0).getStatus());
    }
    @Test
    void approveCustomer_shouldApproveCustomerSuccessfully() {
        ApproveCustomerRequest request = new ApproveCustomerRequest(1L, BigDecimal.valueOf(100), BigDecimal.valueOf(1000));

        User mockUser = new User();
        mockUser.setId(1L);

        when(userService.findById(1L)).thenReturn(mockUser);

        ResponseEntity<GlobalApiResponse> response = employeeController.approveCustomer(request);

        verify(employeeService).approveCustomer(mockUser, BigDecimal.valueOf(100), BigDecimal.valueOf(1000));

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Customer approved and accounts created.", response.getBody().getMessage());
    }

    @Test
    void approveCustomer_shouldThrowUserNotFound_whenUserDoesNotExist() {
        ApproveCustomerRequest request = new ApproveCustomerRequest(99L, BigDecimal.valueOf(100), BigDecimal.valueOf(1000));


        when(userService.findById(99L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> employeeController.approveCustomer(request));
        verify(employeeService, never()).approveCustomer(any(), any(), any());
    }

    @Test
    void declineCustomer_shouldUpdateUserStatusToDenied() {
        User mockUser = new User();
        mockUser.setId(1L);

        when(userService.findById(1L)).thenReturn(mockUser);

        ResponseEntity<GlobalApiResponse> response = employeeController.declineCustomerStatus(1L);

        verify(employeeService).updateUserStatus(mockUser, CustomerStatus.Denied);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("User status updated to Denied", response.getBody().getMessage());
    }


    @Test
    void declineCustomer_shouldThrowUserNotFound_whenUserDoesNotExist() {
        when(userService.findById(123L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> employeeController.declineCustomerStatus(123L));
        verify(employeeService, never()).updateUserStatus(any(), any());
    }
}
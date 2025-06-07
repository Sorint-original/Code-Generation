package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.AccountStatus;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.BankAccountService;
import com.bankapp.Backend.service.TransactionService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    @InjectMocks
    private EmployeeController employeeController;

    @Mock
    private BankAccountService bankAccountService;

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
}
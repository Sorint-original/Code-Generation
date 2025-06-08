package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.AccountInfoResponse;
import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.AccountStatus;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.service.BankAccountService;
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
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    @InjectMocks
    private BankAccountController bankAccountController;

    @Mock
    private BankAccountService bankAccountService;

    private BankAccount testAccount;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUserName("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPhoneNumber("1234567890");

        testAccount = new BankAccount();
        testAccount.setId(100L);
        testAccount.setUser(testUser);
        testAccount.setAmount(BigDecimal.valueOf(500));
        testAccount.setType(AccountType.CHECKING);
        testAccount.setIban("NL01TEST0123456789");
        testAccount.setAbsoluteTransferLimit(BigDecimal.valueOf(1000));
        testAccount.setDailyTransferLimit(BigDecimal.valueOf(500));
        testAccount.setStatus(AccountStatus.APPROVED);
    }

    @Test
    void getAccountInfo_ShouldReturnAccountInfoResponse() {
        List<BankAccount> accountList = List.of(testAccount);
        AccountInfoResponse response = new AccountInfoResponse(
                testUser.getId(),
                testUser.getFirstName(),
                testUser.getLastName(),
                testUser.getUserName(),
                testUser.getEmail(),
                testUser.getPhoneNumber(),
                List.of(new BankAccountResponse(
                        testAccount.getId(),
                        testUser.getId(),
                        testAccount.getAmount(),
                        testAccount.getAccountType(),
                        testAccount.getIban(),
                        testAccount.getAbsoluteTransferLimit(),
                        testAccount.getDailyTransferLimit(),
                        testAccount.getStatus()
                ))
        );

        when(bankAccountService.getCurrentUserId()).thenReturn(1L);
        when(bankAccountService.getBankAccountsByUserId(1L)).thenReturn(accountList);
        when(bankAccountService.infoToResponse(accountList)).thenReturn(response);

        ResponseEntity<AccountInfoResponse> result = bankAccountController.getAccountInfo();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());

        verify(bankAccountService).getCurrentUserId();
        verify(bankAccountService).getBankAccountsByUserId(1L);
        verify(bankAccountService).infoToResponse(accountList);
    }
}


package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.AccountInfoResponse;
import com.bankapp.Backend.DTO.BankAccountResponse;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.security.MyUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BankAccountServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private BankAccountService bankAccountService;
    @Mock
    private MyUserDetails myUserDetails;
    @Mock
    private Authentication authentication;

    @BeforeEach
    public void setUp() {
        bankAccountRepository = mock(BankAccountRepository.class);
        bankAccountService = new BankAccountService(bankAccountRepository);

        SecurityContext context = Mockito.mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(context);
    }


    @Test
    void getAllBankAccounts_ShouldReturnAccounts() {
        List<BankAccount> mockAccounts = List.of(new BankAccount(), new BankAccount());
        when(bankAccountRepository.findAllBankAccounts()).thenReturn(mockAccounts);

        List<BankAccount> result = bankAccountService.getAllBankAccounts();

        assertEquals(2, result.size());
        verify(bankAccountRepository, times(1)).findAllBankAccounts();
    }

    @Test
    void getBankAccountsByUserId_ShouldReturnUserAccounts() {
        Long userId = 1L;
        List<BankAccount> mockAccounts = List.of(new BankAccount());
        when(bankAccountRepository.findBankAccountsByUserId(userId)).thenReturn(mockAccounts);

        List<BankAccount> result = bankAccountService.getBankAccountsByUserId(userId);

        assertEquals(1, result.size());
        verify(bankAccountRepository).findBankAccountsByUserId(userId);
    }

    @Test
    void updateAccountStatus_ShouldCallRepository() {
        String iban = "NL91ABNA0417164300";
        AccountStatus status = AccountStatus.BLOCKED;

        when(bankAccountRepository.existsByIban(iban)).thenReturn(true);

        bankAccountService.updateAccountStatus(iban, status);

        verify(bankAccountRepository).updateStatusByIban(iban, status);
    }

    @Test
    void accountsToResponses_ShouldMapFieldsCorrectly() {
        User user = new User();
        user.setId(1L);

        BankAccount account = new BankAccount();
        account.setId(10L);
        account.setIban("TESTIBAN");
        account.setAmount(BigDecimal.valueOf(1000));
        account.setUser(user);
        account.setType(AccountType.SAVINGS);
        account.setAbsoluteTransferLimit(BigDecimal.TEN);
        account.setDailyTransferLimit(BigDecimal.ONE);
        account.setStatus(AccountStatus.APPROVED);

        List<BankAccountResponse> responses = bankAccountService.accountsToResponses(List.of(account));

        assertEquals(1, responses.size());
        assertEquals("TESTIBAN", responses.get(0).getIban());
    }

    @Test
    void infoToResponse_ShouldBuildAccountInfo() {
        User user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUserName("johndoe");
        user.setEmail("john@example.com");
        user.setPhoneNumber("123456789");

        BankAccount account = new BankAccount();
        account.setUser(user);

        AccountInfoResponse response = bankAccountService.infoToResponse(List.of(account));

        assertEquals("John", response.getFirstName());
        assertEquals("johndoe", response.getUsername());
        assertEquals(1, response.getBankAccounts().size());
    }

}
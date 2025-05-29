package com.bankapp.Backend.service;

import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BankAccountServiceTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @InjectMocks
    private BankAccountService bankAccountService;

    @BeforeEach
    public void setUp() {
        bankAccountRepository = mock(BankAccountRepository.class);
        bankAccountService = new BankAccountService(bankAccountRepository);
    }

    @Test
    public void testApproveCustomer_Success() {
        // Arrange
        User customer = new User();
        customer.setRole(Role.CUSTOMER);
        customer.setBankAccounts(Collections.emptyList());

        // Act
        bankAccountService.approveCustomer(customer);

        // Assert
        ArgumentCaptor<BankAccount> captor = ArgumentCaptor.forClass(BankAccount.class);
        verify(bankAccountRepository, times(2)).save(captor.capture());

        assertEquals(2, captor.getAllValues().size());
        assertTrue(
                captor.getAllValues().stream()
                        .anyMatch(acc -> acc.getAccountType() == AccountType.CHECKING)
        );
        assertTrue(
                captor.getAllValues().stream()
                        .anyMatch(acc -> acc.getAccountType() == AccountType.SAVINGS)
        );
    }

    @Test
    public void testApproveCustomer_WrongRole_ThrowsException() {
        User employee = new User();
        employee.setRole(Role.EMPLOYEE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> bankAccountService.approveCustomer(employee));

        assertEquals("Only customers can be approved.", exception.getMessage());
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    public void testApproveCustomer_AlreadyHasAccounts_ThrowsException() {
        User customer = new User();
        customer.setRole(Role.CUSTOMER);
        BankAccount dummyAccount = new BankAccount();
        customer.setBankAccounts(Collections.singletonList(dummyAccount));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> bankAccountService.approveCustomer(customer));

        assertEquals("Customer already has accounts.", exception.getMessage());
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    public void testChangeDailyLimit_CallsRepositoryCorrectly() {
        String iban = "NL12INHO0123456789";
        BigDecimal newLimit = new BigDecimal("5000");

        bankAccountService.changeDailyLimit(iban, newLimit);

        verify(bankAccountRepository, times(1)).updateDailyLimitByIban(iban, newLimit);
    }
}
package com.bankapp.Backend.service;

import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    void getAllBankAccounts_ShouldReturnAllAccounts() {
        List<BankAccount> mockAccounts = List.of(new BankAccount(), new BankAccount());
        when(bankAccountRepository.findAll()).thenReturn(mockAccounts);

        List<BankAccount> result = bankAccountService.getAllBankAccounts();

        assertEquals(2, result.size());
        verify(bankAccountRepository, times(1)).findAll();
    }

    @Test
    void getBankAccountsByUserId_ShouldReturnUserAccounts() {
        Long userId = 1L;
        List<BankAccount> mockAccounts = List.of(new BankAccount());
        when(bankAccountRepository.findBankAccountsByUserId(userId)).thenReturn(mockAccounts);

        List<BankAccount> result = bankAccountService.getBankAccountsByUserId(userId);

        assertFalse(result.isEmpty());
        verify(bankAccountRepository).findBankAccountsByUserId(userId);
    }

    @Test
    void updateAccountStatus_ShouldUpdateStatusToBlocked() {
        String iban = "NL91ABNA0417164300";

        bankAccountService.updateAccountStatus(iban, AccountStatus.BLOCKED);

        verify(bankAccountRepository).updateStatusByIban(iban, AccountStatus.BLOCKED);
    }

}
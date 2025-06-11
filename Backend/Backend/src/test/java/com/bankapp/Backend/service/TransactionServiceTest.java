package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.TransactionRequest;
import com.bankapp.Backend.exception.CustomerTransactionException;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.TransactionRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private BankAccount fromAccount;
    private BankAccount toAccount;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUserName("johndoe");
        testUser.setEmail("initiator@example.com");
        testUser.setPhoneNumber("1234567890");
        testUser.setRole(Role.CUSTOMER);

        fromAccount = new BankAccount();
        fromAccount.setId(100L);
        fromAccount.setUser(testUser);
        fromAccount.setAmount(BigDecimal.valueOf(1000));
        fromAccount.setType(AccountType.CHECKING);
        fromAccount.setIban("FROM123");
        fromAccount.setAbsoluteTransferLimit(BigDecimal.valueOf(100));
        fromAccount.setDailyTransferLimit(BigDecimal.valueOf(500));
        fromAccount.setStatus(AccountStatus.APPROVED);

        toAccount = new BankAccount();
        toAccount.setId(101L);
        toAccount.setUser(new User()); // different user
        toAccount.setAmount(BigDecimal.valueOf(500));
        toAccount.setType(AccountType.CHECKING);
        toAccount.setIban("TO456");
        toAccount.setAbsoluteTransferLimit(BigDecimal.valueOf(1000));
        toAccount.setDailyTransferLimit(BigDecimal.valueOf(500));
        toAccount.setStatus(AccountStatus.APPROVED);
    }

    @Test
    void transferFundsEmployee_ShouldTransferSuccessfully() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(100));
        request.setInitiatorEmail("initiator@example.com");

        when(bankAccountRepository.findByIban("FROM123")).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findByIban("TO456")).thenReturn(Optional.of(toAccount));
        when(transactionRepository.getTotalTransferredToday(fromAccount)).thenReturn(BigDecimal.ZERO);
        when(userRepository.findUserByEmail("initiator@example.com")).thenReturn(Optional.of(testUser));

        transactionService.transferFundsEmployee(request);

        assertEquals(BigDecimal.valueOf(900), fromAccount.getAmount());
        assertEquals(BigDecimal.valueOf(600), toAccount.getAmount());

        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void transferFundsEmployee_ShouldThrow_WhenFromAccountNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(100));
        request.setInitiatorEmail("initiator@example.com");

        when(bankAccountRepository.findByIban("FROM123")).thenReturn(Optional.empty());

        Exception ex = assertThrows(CustomerTransactionException.class, () ->
                transactionService.transferFundsEmployee(request)
        );

        assertTrue(ex.getMessage().contains("From account not found"));
    }

    @Test
    void transferFundsEmployee_ShouldThrow_WhenToAccountNotFound() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(100));
        request.setInitiatorEmail("initiator@example.com");

        when(bankAccountRepository.findByIban("FROM123")).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findByIban("TO456")).thenReturn(Optional.empty());

        Exception ex = assertThrows(CustomerTransactionException.class, () ->
                transactionService.transferFundsEmployee(request)
        );

        assertTrue(ex.getMessage().contains("To account not found"));
    }

    @Test
    void transferFundsEmployee_ShouldThrow_WhenAccountTypeIsSavings() {
        fromAccount.setType(AccountType.SAVINGS); // Invalid case

        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(100));
        request.setInitiatorEmail("initiator@example.com");

        when(bankAccountRepository.findByIban("FROM123")).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findByIban("TO456")).thenReturn(Optional.of(toAccount));

        Exception ex = assertThrows(CustomerTransactionException.class, () ->
                transactionService.transferFundsEmployee(request)
        );

        assertEquals("Cannot transfer to or from Saving account.", ex.getMessage());
    }

    @Test
    void transferFunds_CustomerInsufficientFunds_ShouldThrow() {
        fromAccount.setAmount(BigDecimal.valueOf(50));

        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(100));
        request.setInitiatorEmail("initiator@example.com");

        when(bankAccountRepository.findByIban("FROM123")).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findByIban("TO456")).thenReturn(Optional.of(toAccount));

        Exception ex = assertThrows(CustomerTransactionException.class, () ->
                transactionService.transferFundsEmployee(request)
        );

        assertTrue(ex.getMessage().contains("Insufficient funds"));
    }


    @Test
    void transferFunds_CustomerBelowAbsoluteLimit_ShouldThrow() {
        fromAccount.setAmount(BigDecimal.valueOf(200));
        fromAccount.setAbsoluteTransferLimit(BigDecimal.valueOf(200));

        TransactionRequest request = new TransactionRequest();
        request.setFromAccountIban("FROM123");
        request.setToAccountIban("TO456");
        request.setAmount(BigDecimal.valueOf(1));
        request.setInitiatorEmail("initiator@example.com");

        when(bankAccountRepository.findByIban("FROM123")).thenReturn(Optional.of(fromAccount));
        when(bankAccountRepository.findByIban("TO456")).thenReturn(Optional.of(toAccount));

        Exception ex = assertThrows(CustomerTransactionException.class, () ->
                transactionService.transferFundsEmployee(request)
        );

        assertTrue(ex.getMessage().contains("Transfer must exceed absolute transfer limit"));
    }

}

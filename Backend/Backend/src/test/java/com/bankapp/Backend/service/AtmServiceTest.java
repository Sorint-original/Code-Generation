package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.AtmRequest;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.Transaction;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.TransactionRepository;
import com.bankapp.Backend.repository.UserRepository;
import com.bankapp.Backend.security.AuthUtils;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtmServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AtmService atmService;

    private AutoCloseable mocks;
    private MockedStatic<AuthUtils> mockedAuthUtils;

    private final Long mockUserId = 1L;

    @BeforeEach
    void setup() {
        mocks = MockitoAnnotations.openMocks(this);

        // Register static mock ONCE per test class
        mockedAuthUtils = mockStatic(AuthUtils.class);
        mockedAuthUtils.when(AuthUtils::getCurrentUserId).thenReturn(mockUserId);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockedAuthUtils.close(); // This prevents multiple registrations
        mocks.close();
    }

    @Test
    void testSuccessfulWithdrawal() {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("100"));

        BankAccount account = new BankAccount();
        account.setIban("IBAN123");
        account.setAmount(new BigDecimal("200"));
        User user = new User();
        user.setId(mockUserId);
        account.setUser(user);

        when(bankAccountRepository.findByIban("IBAN123")).thenReturn(Optional.of(account));
        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(user));

        String result = atmService.withdraw(request);

        assertEquals("Withdrawal successful. New balance: 100", result);
        assertEquals(new BigDecimal("100"), account.getAmount());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testWithdrawalInsufficientFunds() {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("300"));

        BankAccount account = new BankAccount();
        account.setIban("IBAN123");
        account.setAmount(new BigDecimal("200"));
        User user = new User();
        user.setId(mockUserId);
        account.setUser(user);

        when(bankAccountRepository.findByIban("IBAN123")).thenReturn(Optional.of(account));

        String result = atmService.withdraw(request);

        assertEquals("Insufficient funds", result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testWithdrawalUnauthorizedUser() {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("100"));

        User anotherUser = new User();
        anotherUser.setId(2L);
        BankAccount account = new BankAccount();
        account.setIban("IBAN123");
        account.setAmount(new BigDecimal("200"));
        account.setUser(anotherUser);

        when(bankAccountRepository.findByIban("IBAN123")).thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> atmService.withdraw(request));
        assertEquals("Unauthorized access to this account", ex.getMessage());
    }

    @Test
    void testDepositSuccess() {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("150"));

        BankAccount account = new BankAccount();
        account.setIban("IBAN123");
        account.setAmount(new BigDecimal("350"));
        User user = new User();
        user.setId(mockUserId);
        account.setUser(user);

        when(bankAccountRepository.findByIban("IBAN123")).thenReturn(Optional.of(account));
        when(userRepository.findById(mockUserId)).thenReturn(Optional.of(user));

        String result = atmService.deposit(request);

        assertEquals("Deposit successful. New balance: 500", result);
        assertEquals(new BigDecimal("500"), account.getAmount());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void testDepositUnauthorizedUser() {
        AtmRequest request = new AtmRequest("IBAN123", new BigDecimal("100"));

        User anotherUser = new User();
        anotherUser.setId(2L);
        BankAccount account = new BankAccount();
        account.setIban("IBAN123");
        account.setAmount(new BigDecimal("1000"));
        account.setUser(anotherUser);

        when(bankAccountRepository.findByIban("IBAN123")).thenReturn(Optional.of(account));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> atmService.deposit(request));
        assertEquals("Unauthorized access to this account", ex.getMessage());
    }
}

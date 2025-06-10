package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.CustomerIbanRequest;
import com.bankapp.Backend.DTO.CustomerIbanResponse;
import com.bankapp.Backend.DTO.RecipientAccount;
import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
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
class CustomerServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomerService customerService;

    // ---------- getIbansByName ----------

    @Test
    void testGetIbansByNameReturnsCorrectIbans() {
        CustomerIbanRequest request = new CustomerIbanRequest("John", "Doe");

        User user = new User();
        BankAccount acc1 = new BankAccount(user, AccountType.CHECKING, "NL01BANK1234567890", BigDecimal.TEN, BigDecimal.TEN);
        BankAccount acc2 = new BankAccount(user, AccountType.SAVINGS, "NL02BANK9876543210", BigDecimal.TEN, BigDecimal.TEN);
        user.setBankAccounts(List.of(acc1, acc2));

        when(userRepository.findUserByFirstNameAndLastName("John", "Doe")).thenReturn(Optional.of(user));

        List<CustomerIbanResponse> result = customerService.getIbansByName(request);

        assertEquals(2, result.size());
        assertEquals("NL01BANK1234567890", result.get(0).getIban());
        assertEquals("CHECKING", result.get(0).getAccountType());
    }

    @Test
    void testGetIbansByNameThrowsIfUserNotFound() {
        CustomerIbanRequest request = new CustomerIbanRequest("Jane", "Smith");
        when(userRepository.findUserByFirstNameAndLastName("Jane", "Smith")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> customerService.getIbansByName(request),
                "Customer or IBAN not found.");
    }

    @Test
    void testGetIbansByNameWithEmptyFirstNameThrows() {
        CustomerIbanRequest request = new CustomerIbanRequest("", "Smith");
        when(userRepository.findUserByFirstNameAndLastName("", "Smith")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> customerService.getIbansByName(request));
    }

    @Test
    void testGetIbansByNameWithNullRequestThrows() {
        assertThrows(NullPointerException.class, () -> customerService.getIbansByName(null));
    }

    @Test
    void testGetIbansByNameReturnsEmptyListIfUserHasNoAccounts() {
        CustomerIbanRequest request = new CustomerIbanRequest("Empty", "User");
        User user = new User();
        user.setBankAccounts(List.of()); // No accounts

        when(userRepository.findUserByFirstNameAndLastName("Empty", "User"))
                .thenReturn(Optional.of(user));

        List<CustomerIbanResponse> results = customerService.getIbansByName(request);

        assertEquals(0, results.size());
    }

    // ---------- searchRecipientAccounts ----------

    @Test
    void testSearchRecipientAccountsReturnsMatchingUsers() {
        User user = new User();
        user.setFirstName("Alice");
        user.setLastName("Wonderland");
        BankAccount account = new BankAccount(user, AccountType.CHECKING, "NL03BANK0000000001", BigDecimal.TEN, BigDecimal.TEN);
        user.setBankAccounts(List.of(account));

        when(userRepository.findAll()).thenReturn(List.of(user));

        List<RecipientAccount> results = customerService.searchRecipientAccounts("ali");

        assertEquals(1, results.size());
        assertEquals("Alice Wonderland", results.get(0).getFullName());
    }

    @Test
    void testSearchRecipientAccountsQueryTooShortThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> customerService.searchRecipientAccounts("a"));
    }

    @Test
    void testSearchRecipientAccountsWithSpecialCharsTooShortThrows() {
        // "b@#" -> cleaned: "b" -> length < 2 → exception expected
        assertThrows(IllegalArgumentException.class, () ->
                        customerService.searchRecipientAccounts("b@#"),
                "Search query must be at least 2 letters.");
    }

    @Test
    void testSearchRecipientAccountsReturnsEmptyIfNoMatches() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<RecipientAccount> results = customerService.searchRecipientAccounts("xyz");

        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchRecipientAccountsWithNullQueryThrows() {
        assertThrows(NullPointerException.class, () -> customerService.searchRecipientAccounts(null));
    }
}

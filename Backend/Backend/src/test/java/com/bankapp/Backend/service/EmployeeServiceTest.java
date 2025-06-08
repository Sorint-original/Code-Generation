package com.bankapp.Backend.service;

import com.bankapp.Backend.exception.*;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private IBANGenerator ibanGenerator;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void approveCustomer_ShouldCreateAccountsAndUpdateStatus_WhenValid() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);
        user.setBankAccounts(new ArrayList<>());

        when(ibanGenerator.generateDutchIBAN()).thenReturn("NL01TEST0123456789");

        // Act
        employeeService.approveCustomer(user, BigDecimal.valueOf(10000), BigDecimal.valueOf(5000));

        // Assert
        verify(userRepository).updateUserStatusById(1L, CustomerStatus.Approved);
        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
    }


    @Test
    void approveCustomer_ShouldThrow_WhenUserIsNotCustomer() {
        User user = new User();
        user.setId(2L);
        user.setRole(Role.EMPLOYEE);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        InvalidUserRoleException ex = assertThrows(InvalidUserRoleException.class, () ->
                employeeService.approveCustomer(user, BigDecimal.TEN, BigDecimal.TEN)
        );

        assertEquals("Only customers can be approved.", ex.getMessage());
    }

    @Test
    void approveCustomer_ShouldThrow_WhenUserHasExistingAccounts() {
        User user = new User();
        user.setId(3L);
        user.setRole(Role.CUSTOMER);
        user.setBankAccounts(List.of(new BankAccount()));

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));

        UserAlreadyHasAccountException ex = assertThrows(UserAlreadyHasAccountException.class, () ->
                employeeService.approveCustomer(user, BigDecimal.TEN, BigDecimal.TEN)
        );

        assertEquals("Customer already has accounts.", ex.getMessage());
    }

    @Test
    void approveCustomer_ShouldThrow_WhenLimitsAreNull() {
        User user = new User();
        user.setId(4L);
        user.setRole(Role.CUSTOMER);
        user.setBankAccounts(new ArrayList<>());

        when(userRepository.findById(4L)).thenReturn(Optional.of(user));

        InvalidTransferLimitException ex = assertThrows(InvalidTransferLimitException.class, () ->
                employeeService.approveCustomer(user, null, BigDecimal.TEN)
        );

        assertEquals("Transfer limits cannot be null.", ex.getMessage());
    }

    @Test
    void approveCustomer_ShouldThrow_WhenLimitsAreNegative() {
        User user = new User();
        user.setId(5L);
        user.setRole(Role.CUSTOMER);
        user.setBankAccounts(new ArrayList<>());

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        InvalidTransferLimitException ex = assertThrows(InvalidTransferLimitException.class, () ->
                employeeService.approveCustomer(user, BigDecimal.valueOf(-1000), BigDecimal.TEN)
        );

        assertEquals("Transfer limits must be non-negative.", ex.getMessage());
    }

    @Test
    void updateUserStatus_ShouldCallRepositoryCorrectly() {
        User user = new User();
        user.setId(6L);

        employeeService.updateUserStatus(user, CustomerStatus.Denied);

        verify(userRepository, times(1)).updateUserStatusById(6L, CustomerStatus.Denied);
    }
}

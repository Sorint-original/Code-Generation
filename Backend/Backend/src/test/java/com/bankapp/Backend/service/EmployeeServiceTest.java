package com.bankapp.Backend.service;

import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class EmployeeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        employeeService = new EmployeeService(userRepository, bankAccountRepository);
    }

    @Test
    void approveCustomer_ShouldCreateTwoAccounts_AndUpdateStatus() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.CUSTOMER);
        user.setBankAccounts(new ArrayList<>());

        employeeService.approveCustomer(user);

        verify(userRepository, times(1)).updateUserStatusById(1L, CustomerStatus.Approved);
        verify(bankAccountRepository, times(2)).save(any(BankAccount.class));
    }

    @Test
    void approveCustomer_ShouldThrowException_WhenNotCustomer() {
        User user = new User();
        user.setId(2L);
        user.setRole(Role.EMPLOYEE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                employeeService.approveCustomer(user)
        );

        assertEquals("Only customers can be approved.", exception.getMessage());
    }

    @Test
    void approveCustomer_ShouldThrowException_WhenAlreadyHasAccounts() {
        User user = new User();
        user.setId(3L);
        user.setRole(Role.CUSTOMER);
        ArrayList<BankAccount> existing = new ArrayList<>();
        existing.add(new BankAccount());
        user.setBankAccounts(existing);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                employeeService.approveCustomer(user)
        );

        assertEquals("Customer already has accounts.", exception.getMessage());
    }

    @Test
    void updateUserStatus_ShouldCallRepositoryMethod() {
        User user = new User();
        user.setId(4L);

        employeeService.updateUserStatus(user, CustomerStatus.Denied);

        verify(userRepository, times(1)).updateUserStatusById(4L, CustomerStatus.Denied);
    }
}

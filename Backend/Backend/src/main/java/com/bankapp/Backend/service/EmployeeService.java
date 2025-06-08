package com.bankapp.Backend.service;

import com.bankapp.Backend.exception.InvalidTransferLimitException;
import com.bankapp.Backend.exception.InvalidUserRoleException;
import com.bankapp.Backend.exception.UserAlreadyHasAccountException;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final IBANGenerator ibanGenerator;

    public EmployeeService(UserRepository userRepository, BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.ibanGenerator = new IBANGenerator();
    }

    public void approveCustomer(User user, BigDecimal absoluteTransferLimit, BigDecimal dailyTransferLimit) {

        if (absoluteTransferLimit == null || dailyTransferLimit == null) {
            throw new InvalidTransferLimitException("Transfer limits cannot be null.");
        }

        if (absoluteTransferLimit.compareTo(BigDecimal.ZERO) < 0 || dailyTransferLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTransferLimitException("Transfer limits must be non-negative.");
        }
        if (user.getRole() != Role.CUSTOMER) {
            throw new InvalidUserRoleException("Only customers can be approved.");
        }

        if (!user.getBankAccounts().isEmpty()) {
            throw new UserAlreadyHasAccountException();
        }

        userRepository.updateUserStatusById(user.getId(), CustomerStatus.Approved);

        BankAccount checking = new BankAccount(user, AccountType.CHECKING, ibanGenerator.generateDutchIBAN(), absoluteTransferLimit, dailyTransferLimit);
        BankAccount savings = new BankAccount(user, AccountType.SAVINGS, ibanGenerator.generateDutchIBAN(), absoluteTransferLimit, dailyTransferLimit);

        bankAccountRepository.save(checking);
        bankAccountRepository.save(savings);
    }

    public void updateUserStatus(User user, CustomerStatus status) {
        userRepository.updateUserStatusById(user.getId(), status);
    }
}

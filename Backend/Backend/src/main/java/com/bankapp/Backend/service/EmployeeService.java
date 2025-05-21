package com.bankapp.Backend.service;


import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.stereotype.Service;

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

    public void approveCustomer(User user) {

        if (user.getRole() != Role.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can be approved.");
        }

        if (!user.getBankAccounts().isEmpty()) {
            throw new IllegalStateException("Customer already has accounts.");
        }



        userRepository.updateUserStatusById(user.getId(), CustomerStatus.Approved);
        BankAccount checking = new BankAccount(user, AccountType.CHECKING, ibanGenerator.generateDutchIBAN());
        BankAccount savings = new BankAccount(user, AccountType.SAVINGS, ibanGenerator.generateDutchIBAN());


        bankAccountRepository.save(checking);
        bankAccountRepository.save(savings);
    }

    public void updateUserStatus(User user, CustomerStatus status) {
        userRepository.updateUserStatusById(user.getId(), status);
    }


}

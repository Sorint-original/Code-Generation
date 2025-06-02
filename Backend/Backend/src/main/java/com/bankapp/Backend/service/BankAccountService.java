package com.bankapp.Backend.service;

import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import com.bankapp.Backend.security.MyUserDetails;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final IBANGenerator ibanGenerator;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
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



        BankAccount checking = new BankAccount(user, AccountType.CHECKING, ibanGenerator.generateDutchIBAN());
        BankAccount savings = new BankAccount(user, AccountType.SAVINGS, ibanGenerator.generateDutchIBAN());

        bankAccountRepository.save(checking);
        bankAccountRepository.save(savings);
    }
    public void changeDailyLimit(String iban, BigDecimal newLimit) {
        bankAccountRepository.updateDailyLimitByIban(iban, newLimit);
    }

    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAllBankAccounts();
    }

    public List<BankAccount> getBankAccountsByUserId(Long userId) {
        return bankAccountRepository.findBankAccountsByUserId(userId);
    }

    public void updateAccountStatus(String iban, AccountStatus status) {
        bankAccountRepository.updateStatusByIban(iban, status);
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            MyUserDetails userDetails = (MyUserDetails) auth.getPrincipal();
            return userDetails.getUserId();
        }

        throw new RuntimeException("Unauthorized");
    }
}
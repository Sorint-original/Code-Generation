package com.bankapp.Backend.service;

import com.bankapp.Backend.model.AccountType;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final IBANGenerator ibanGenerator;

    public BankAccountService(BankAccountRepository bankAccountRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.ibanGenerator = new IBANGenerator();
    }

    public Optional<BankAccount> GetBankAccount(String iban) {
       return bankAccountRepository.findByIban(iban);
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
    public void changeDailyLimit(BankAccount bankAccount, BigDecimal newLimit) {
        bankAccountRepository.updateDailyLimitByIban(bankAccount.getIban(), newLimit);
    }
}
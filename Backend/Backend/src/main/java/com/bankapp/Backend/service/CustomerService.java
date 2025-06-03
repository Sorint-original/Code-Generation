package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.CustomerIbanRequest;
import com.bankapp.Backend.DTO.CustomerIbanResponse;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomerService(BankAccountRepository bankAccountRepository, UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public List<CustomerIbanResponse> getIbansByName(CustomerIbanRequest request) {
        return userRepository.findUserByFirstNameAndLastName(request.getFirstName(), request.getLastName())
                .map(user -> user.getBankAccounts().stream()
                        .map(acc -> new CustomerIbanResponse(acc.getIban(), acc.getType().toString()))
                        .collect(Collectors.toList())
                ).orElseThrow(() -> new IllegalArgumentException("Customer or IBAN not found."));
    }



}

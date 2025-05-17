package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import com.bankapp.Backend.DTO.CustomerRegistrationResponse;
import com.bankapp.Backend.model.BankAccount;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.BankAccountRepository;
import com.bankapp.Backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public final UserRepository userRepository;
    public final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final BankAccountService bankAccountService;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, BankAccountService bankAccountService) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.bankAccountService = bankAccountService;
    }

    public User createUser(User user) {
        validateUniqueFields(user);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public CustomerRegistrationResponse registerCustomer(CustomerRegistrationRequest request) {
        // Map request to User
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setBsnNumber(request.getBsnNumber());
        newUser.setUserName(request.getUserName());
        newUser.setRole(Role.CUSTOMER);

        try {
            // Validate & save user
            User savedUser = createUser(newUser);

            // Create default account
            BankAccount account = bankAccountService.createDefaultBankAccount(savedUser);
            savedUser.setBankAccounts(List.of(account));

            return new CustomerRegistrationResponse(
                    savedUser.getEmail(),
                    savedUser.getUserName(),
                    true,
                    "Registration successful. Your account is pending approval."
            );
        } catch (IllegalArgumentException e) {
            return new CustomerRegistrationResponse(
                    request.getEmail(),
                    request.getUserName(),
                    false,
                    e.getMessage()
            );
        } catch (Exception e) {
            return new CustomerRegistrationResponse(
                    request.getEmail(),
                    request.getUserName(),
                    false,
                    "An unexpected error occurred. Please try again later."
            );
        }
    }



    public void validateUniqueFields(User user) {
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        if (userRepository.findUserByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new IllegalArgumentException("Phone number is already in use.");
        }
        if (userRepository.findUserByBsnNumber(user.getBsnNumber()).isPresent()) {
            throw new IllegalArgumentException("BSN number is already in use.");
        }
    }

}

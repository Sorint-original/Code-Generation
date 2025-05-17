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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public final UserRepository userRepository;
    public final BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
        newUser.setBankAccounts(new ArrayList<BankAccount>());

        try {
            // Validate & save user
            User savedUser = createUser(newUser);


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

    public List<User> findUnapprovedUsers(Role role) {
        List<User> users = userRepository.findAllByRoleAndBankAccountsEmpty(role);

        if (users.isEmpty()) {
            throw new IllegalStateException("No unapproved users found.");
        }

        return users;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}

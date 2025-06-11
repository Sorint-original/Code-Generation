package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import com.bankapp.Backend.DTO.CustomerRegistrationResponse;
import com.bankapp.Backend.DTO.DashboardStatusResponse;
import com.bankapp.Backend.exception.DuplicateFieldException;
import com.bankapp.Backend.exception.ForbiddenActionException;
import com.bankapp.Backend.exception.StatusNotFoundException;
import com.bankapp.Backend.exception.UserNotFoundException;
import com.bankapp.Backend.model.*;
import com.bankapp.Backend.repository.UserRepository;
import com.bankapp.Backend.security.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
        validateDigitsOnly(request.getPhoneNumber(), "Phone number");
        validateDigitsOnly(request.getBsnNumber(), "BSN number");
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(request.getPassword());
        newUser.setPhoneNumber(request.getPhoneNumber());
        newUser.setBsnNumber(request.getBsnNumber());
        newUser.setUserName(request.getUserName());
        newUser.setRole(Role.CUSTOMER);
        newUser.setBankAccounts(new ArrayList<>());
        newUser.setStatus(CustomerStatus.Pending);

        User savedUser = createUser(newUser);
        return new CustomerRegistrationResponse(
                savedUser,
                true,
                "Registration successful. Your account is pending approval."
        );
    }
    private void validateDigitsOnly(String value, String fieldName) {
        if (!value.matches("\\d+")) {
            throw new IllegalArgumentException(fieldName + " must contain only digits.");
        }
    }


    public void validateUniqueFields(User user) {
        userRepository.findUserByEmail(user.getEmail())
                .ifPresent(u -> { throw new DuplicateFieldException("Email", user.getEmail()); });

        userRepository.findUserByPhoneNumber(user.getPhoneNumber())
                .ifPresent(u -> { throw new DuplicateFieldException("Phone number", user.getPhoneNumber()); });

        userRepository.findUserByBsnNumber(user.getBsnNumber())
                .ifPresent(u -> { throw new DuplicateFieldException("BSN number", user.getBsnNumber()); });
    }

    public List<User> findUnapprovedUsers(Role role) {
        return userRepository.findAllByRoleAndStatus(role, CustomerStatus.Pending);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    public long getCurrentUserId() {
        return AuthUtils.getCurrentUserId();
    }

    public DashboardStatusResponse getCustomerDashboardStatus() {
        Long id = AuthUtils.getCurrentUserId();

        CustomerStatus status = userRepository.findStatusById(id).orElseThrow(() -> new StatusNotFoundException("Status of user was not found"));

        return switch (status) {
            case Pending -> new DashboardStatusResponse("Your account is pending approval.");
            case Denied -> new DashboardStatusResponse("Your account has been denied. Please contact support.");
            case Approved -> new DashboardStatusResponse("Welcome to your dashboard!");
        };
    }
}

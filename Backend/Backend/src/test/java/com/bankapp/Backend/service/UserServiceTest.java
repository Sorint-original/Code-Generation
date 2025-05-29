package com.bankapp.Backend.service;

import com.bankapp.Backend.DTO.CustomerRegistrationRequest;
import com.bankapp.Backend.DTO.CustomerRegistrationResponse;
import com.bankapp.Backend.model.CustomerStatus;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_shouldSaveUserWithEncodedPassword() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("rawpass");
        user.setPhoneNumber("12345");
        user.setBsnNumber("987654321");

        when(userRepository.findUserByEmail("test@example.com")).thenReturn(Optional.empty());
        when(userRepository.findUserByPhoneNumber("12345")).thenReturn(Optional.empty());
        when(userRepository.findUserByBsnNumber("987654321")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("encodedpass");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User savedUser = userService.createUser(user);

        assertEquals("encodedpass", savedUser.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void registerCustomer_shouldReturnSuccessResponse() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setUserName("johndoe");
        request.setEmail("john@example.com");
        request.setPassword("password");
        request.setPhoneNumber("1234567890");
        request.setBsnNumber("123456789");

        when(userRepository.findUserByEmail("john@example.com")).thenReturn(Optional.empty());
        when(userRepository.findUserByPhoneNumber("1234567890")).thenReturn(Optional.empty());
        when(userRepository.findUserByBsnNumber("123456789")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        CustomerRegistrationResponse response = userService.registerCustomer(request);

        assertTrue(response.isSuccess());
        assertEquals("Registration successful. Your account is pending approval.", response.getMessage());
        assertEquals(CustomerStatus.Pending, response.getUser().getStatus());
    }

    @Test
    void registerCustomer_shouldReturnFailureIfEmailExists() {
        CustomerRegistrationRequest request = new CustomerRegistrationRequest();
        request.setEmail("exists@example.com");
        request.setPhoneNumber("12345");
        request.setBsnNumber("99999");

        when(userRepository.findUserByEmail("exists@example.com")).thenReturn(Optional.of(new User()));

        CustomerRegistrationResponse response = userService.registerCustomer(request);

        assertFalse(response.isSuccess());
        assertEquals("Email is already in use.", response.getMessage());
    }

    @Test
    void validateUniqueFields_shouldThrowExceptionIfEmailExists() {
        User user = new User();
        user.setEmail("exists@example.com");

        when(userRepository.findUserByEmail("exists@example.com")).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.validateUniqueFields(user);
        });

        assertEquals("Email is already in use.", exception.getMessage());
    }

    @Test
    void findById_shouldReturnUserIfExists() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findById_shouldReturnNullIfUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        User result = userService.findById(99L);
        assertNull(result);
    }

    @Test
    void findUnapprovedUsers_shouldReturnPendingCustomers() {
        Role role = Role.CUSTOMER;
        User user = new User();
        user.setStatus(CustomerStatus.Pending);
        user.setRole(role);

        when(userRepository.findAllByRoleAndStatus(role, CustomerStatus.Pending))
                .thenReturn(List.of(user));

        List<User> result = userService.findUnapprovedUsers(role);
        assertEquals(1, result.size());
        assertEquals(CustomerStatus.Pending, result.get(0).getStatus());
    }
}
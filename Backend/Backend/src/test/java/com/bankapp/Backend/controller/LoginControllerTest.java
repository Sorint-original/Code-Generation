package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.LoginRequest;
import com.bankapp.Backend.DTO.LoginResponse;
import com.bankapp.Backend.model.Role;
import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.UserRepository;
import com.bankapp.Backend.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginControllerTest {

    @InjectMocks
    private LoginController loginController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSuccessfulLogin() {
        // Given
        String email = "test@example.com";
        String password = "password123";
        String token = "fake-jwt-token";

        LoginRequest loginRequest = new LoginRequest(email, password);

        User user = new User();
        user.setEmail(email);
        user.setRole(Role.CUSTOMER);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findUserByEmail(email))
                .thenReturn(Optional.of(user));

        when(jwtProvider.generateToken(user))
                .thenReturn(token);

        // When
        ResponseEntity<LoginResponse> response = loginController.login(loginRequest);

        // Then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(token, response.getBody().getToken());
    }

    @Test
    public void testLogin_UserNotFound() {
        // Given
        String email = "missing@example.com";
        String password = "irrelevant";

        LoginRequest loginRequest = new LoginRequest(email, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userRepository.findUserByEmail(email))
                .thenReturn(Optional.empty());

        // Then
        assertThrows(UsernameNotFoundException.class, () -> {
            loginController.login(loginRequest);
        });
    }
}
package com.bankapp.Backend.service;

import com.bankapp.Backend.model.User;
import com.bankapp.Backend.repository.UserRepository;
import com.bankapp.Backend.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtProvider jwtProvider;
    private UserRepository userRepository;
    private LoginService loginService;


    @BeforeEach
    void setUp() {
        authenticationManager = mock(AuthenticationManager.class);
        jwtProvider = mock(JwtProvider.class);
        userRepository = mock(UserRepository.class);
        loginService = new LoginService(authenticationManager, jwtProvider, userRepository);
    }

    @Test
    void authenticateAndGenerateToken_validCredentials_returnsToken() {
        String email = "user@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.of(user));
        when(jwtProvider.generateToken(user)).thenReturn("mocked-token");

        String token = loginService.authenticateAndGenerateToken(email, password);
        assertEquals("mocked-token", token);
    }

    @Test
    void authenticateAndGenerateToken_userNotFound_throwsException() {
        String email = "notfound@example.com";
        String password = "password";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mock(Authentication.class));

        when(userRepository.findUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                loginService.authenticateAndGenerateToken(email, password));
    }

    @Test
    void authenticateAndGenerateToken_badCredentials_throwsException() {
        String email = "user@example.com";
        String password = "wrong";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () ->
                loginService.authenticateAndGenerateToken(email, password));
    }
}

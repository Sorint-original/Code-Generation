package com.bankapp.Backend.controller;

import com.bankapp.Backend.DTO.LoginRequest;
import com.bankapp.Backend.DTO.LoginResponse;
import com.bankapp.Backend.exception.GlobalExceptionHandler;
import com.bankapp.Backend.service.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class LoginControllerTest {

    private MockMvc mockMvc;
    private LoginService loginService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        loginService = mock(LoginService.class);
        LoginController loginController = new LoginController(loginService);
        objectMapper = new ObjectMapper();

        mockMvc = MockMvcBuilders
                .standaloneSetup(loginController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_validCredentials_returnsToken() throws Exception {
        String email = "user@example.com";
        String password = "pass";
        String token = "mocked-jwt";

        when(loginService.authenticateAndGenerateToken(email, password)).thenReturn(token);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void login_userNotFound_returnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@example.com");
        request.setPassword("anyPassword");

        when(loginService.authenticateAndGenerateToken(anyString(), anyString()))
                .thenThrow(new UsernameNotFoundException("User not found"));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_missingEmail_returnsBadRequest() throws Exception {
        String json = "{ \"password\": \"pass\" }";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_missingPassword_returnsBadRequest() throws Exception {
        String json = "{ \"email\": \"user@example.com\" }";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}

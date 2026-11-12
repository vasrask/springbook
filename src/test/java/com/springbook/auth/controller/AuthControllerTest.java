package com.springbook.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springbook.auth.AuthController;
import com.springbook.auth.AuthService;
import com.springbook.auth.dto.AuthResponse;
import com.springbook.auth.dto.LoginRequest;
import com.springbook.auth.dto.RegisterRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        AuthController controller = new AuthController(authService);
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller).setValidator(validator).build();

        objectMapper = new ObjectMapper().findAndRegisterModules();
    }

    @Test
    void register_shouldReturnCreated_whenRequestIsValid()
            throws Exception {
        RegisterRequest request = new RegisterRequest("test@example.com", "password123", "Test User");
        AuthResponse response = new AuthResponse(1L, "test@example.com", "Test User", "mock-jwt-token");

        when(authService.register(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("test@example.com"))
                .andExpect(jsonPath("$.displayName")
                        .value("Test User"))
                .andExpect(jsonPath("$.token")
                        .value("mock-jwt-token"));

        verify(authService).register(request);
    }

    @Test
    void register_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {
        String invalidRequest = """
                {
                    "email": "invalid-email",
                    "password": "123",
                    "displayName": ""
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(authService, never()).register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturnOk_whenCredentialsAreValid()
            throws Exception {

        LoginRequest request = new LoginRequest("test@example.com", "password123");

        AuthResponse response = new AuthResponse(
                1L,
                "test@example.com",
                "Test User",
                "mock-jwt-token"
        );

        when(authService.login(request)).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email")
                        .value("test@example.com"))
                .andExpect(jsonPath("$.displayName")
                        .value("Test User"))
                .andExpect(jsonPath("$.token")
                        .value("mock-jwt-token"));

        verify(authService).login(request);
    }

    @Test
    void login_shouldReturnBadRequest_whenRequestIsInvalid()
            throws Exception {
        String invalidRequest = """
                {
                    "email": "not-an-email",
                    "password": ""
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());

        verify(authService, never()).login(any(LoginRequest.class));
    }
}
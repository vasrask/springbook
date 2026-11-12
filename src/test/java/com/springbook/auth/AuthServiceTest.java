package com.springbook.auth;

import com.springbook.auth.dto.AuthResponse;
import com.springbook.auth.dto.LoginRequest;
import com.springbook.auth.dto.RegisterRequest;
import com.springbook.exception.DuplicateResourceException;
import com.springbook.security.JwtService;
import com.springbook.user.User;
import com.springbook.user.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();

        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void register_shouldCreateUserAndReturnToken() {

        RegisterRequest request = new RegisterRequest(
                "test@example.com",
                "StrongPassword123",
                "Test User"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(false);

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    ReflectionTestUtils.setField(savedUser, "id", 1L);
                    return savedUser;
                });

        when(jwtService.generateToken(any(User.class))).thenReturn("test-jwt-token");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("test@example.com", response.email());
        assertEquals("Test User", response.displayName());
        assertEquals("test-jwt-token", response.token());

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("Test User", savedUser.getDisplayName());

        assertNotEquals("StrongPassword123", savedUser.getPasswordHash());

        assertTrue(passwordEncoder.matches("StrongPassword123", savedUser.getPasswordHash()));

        verify(jwtService).generateToken(savedUser);
    }


    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest("test@example.com", "StrongPassword123", "Test User");

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any(User.class));

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_shouldReturnToken_whenCredentialsAreValid() {

        String rawPassword = "StrongPassword123";

        User user = createTestUser("test@example.com", rawPassword);

        LoginRequest request = new LoginRequest("test@example.com", rawPassword);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        when(jwtService.generateToken(user)).thenReturn("test-jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("test@example.com", response.email());
        assertEquals("Test User", response.displayName());
        assertEquals("test-jwt-token", response.token());

        verify(userRepository).findByEmail(request.email());
        verify(jwtService).generateToken(user);
    }

    @Test
    void login_shouldThrowException_whenPasswordIsIncorrect() {

        User user = createTestUser("test@example.com", "CorrectPassword123");

        LoginRequest request = new LoginRequest("test@example.com", "WrongPassword123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void login_shouldThrowException_whenEmailDoesNotExist() {

        LoginRequest request = new LoginRequest("unknown@example.com", "StrongPassword123");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(jwtService, never()).generateToken(any(User.class));
    }

    private User createTestUser(String email, String rawPassword) {

        User user = new User();

        ReflectionTestUtils.setField(user, "id", 1L);

        user.setEmail(email);
        user.setDisplayName("Test User");
        user.setPasswordHash(passwordEncoder.encode(rawPassword));

        return user;
    }
}
package com.springbook.auth;

import com.springbook.auth.dto.AuthResponse;
import com.springbook.auth.dto.RegisterRequest;
import com.springbook.exception.DuplicateResourceException;
import com.springbook.security.JwtService;
import com.springbook.user.User;
import com.springbook.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springbook.auth.dto.LoginRequest;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                    "An account with this email already exists");
        }

        User user = new User();

        user.setEmail(request.email());
        user.setPasswordHash(
                passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getDisplayName(),
                jwtService.generateToken(user)
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"));

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new org.springframework.security.authentication
                    .BadCredentialsException(
                    "Invalid email or password");
        }
        String token = jwtService.generateToken(user);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                token
        );
    }
}

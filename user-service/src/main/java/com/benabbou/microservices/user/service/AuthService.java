package com.benabbou.microservices.user.service;

import com.benabbou.microservices.user.model.User;
import com.benabbou.microservices.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registers a new user and returns a JWT token.
     *
     * @param user the user to be registered
     * @return JWT token for the registered user
     */
    public String registerUser(User user) {
        log.info("Attempting to register user with username: {}", user.getUsername());

        if (userRepository.existsByUsername(user.getUsername())) {
            log.warn("Registration failed: Username {} already exists.", user.getUsername());
            throw new IllegalArgumentException("Username already exists.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername() , user.getEmail());
        log.info("User registered successfully with username: {}", user.getUsername());

        return token;
    }

    /**
     * Authenticates the user and returns a JWT token.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return JWT token if authentication is successful
     */
    public String authenticateUser(String username, String password) {
        log.info("Attempting to authenticate user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Authentication failed: Invalid username {}.", username);
                    return new IllegalArgumentException("Invalid username or password.");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("Authentication failed: Password does not match for username {}.", username);
            throw new IllegalArgumentException("Invalid username or password.");
        }

        String token = jwtService.generateToken(username,user.getEmail());
        log.info("User authenticated successfully with username: {}", username);

        return token;
    }

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to validate
     */
    public void validateToken(String token) {
        log.info("Validating token: {}", token);
        jwtService.validateToken(token);
        log.info("Token validated successfully: {}", token);
    }
}

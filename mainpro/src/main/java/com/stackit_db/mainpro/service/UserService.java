package com.stackit_db.mainpro.service;

import com.stackit_db.mainpro.dto.RegisterRequest;
import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.repository.UserRepository; // <-- add this
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationResult register(RegisterRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getEmail())
                || !StringUtils.hasText(request.getPassword())) {
            return new RegistrationResult(HttpStatus.BAD_REQUEST, "Username, email, and password are required.");
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            return new RegistrationResult(HttpStatus.CONFLICT, "Username already exists.");
        }

        if (userRepository.existsByEmail(email)) {
            return new RegistrationResult(HttpStatus.CONFLICT, "Email already exists.");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword())); // BCrypt hash

        userRepository.save(user);

        return new RegistrationResult(HttpStatus.CREATED, "User registered successfully.");
    }

    public record RegistrationResult(HttpStatus status, String message) {
        public Map<String, String> body() {
            Map<String, String> response = new HashMap<>();
            response.put("message", message);
            return response;
        }
    }
}

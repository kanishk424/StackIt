package com.stackit_db.mainpro.controller;

import com.stackit_db.mainpro.dto.AuthResponse;
import com.stackit_db.mainpro.dto.LoginRequest;
import com.stackit_db.mainpro.dto.RegisterRequest;
import com.stackit_db.mainpro.entity.User;
import com.stackit_db.mainpro.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserRegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getUsername())
                || !StringUtils.hasText(request.getEmail())
                || !StringUtils.hasText(request.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username, email, and password are required."));
        }

        String username = request.getUsername().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Username already exists."));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already exists."));
        }

        User user = new User();
        user.setUsername(username);          // required: NOT NULL column
        user.setDisplayName(username);       // optional display label
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User saved = userRepository.save(user);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(saved.getId(), saved.getUsername(), saved.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        if (request == null
                || !StringUtils.hasText(request.getEmail())
                || !StringUtils.hasText(request.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required."));
        }

        String email = request.getEmail().trim().toLowerCase();
        return userRepository.findByEmail(email)
                .map(user -> {
                    boolean ok = passwordEncoder.matches(request.getPassword(), user.getPassword());
                    if (!ok) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of("message", "Invalid email or password."));
                    }
                    return ResponseEntity.ok(new AuthResponse(user.getId(), user.getUsername(), user.getEmail()));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Invalid email or password.")));
    }
}

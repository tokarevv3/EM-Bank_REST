package com.example.bankcards.controller;

import com.example.bankcards.dto.request.LoginRequest;
import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {

    private final RegisterService registerService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDto newUser) {
        log.info("Registering user with login: {}", newUser.getLogin());
        try {
            registerService.registerUser(newUser);
            var jwtResponse = authService.authenticateUser(newUser.getLogin(), newUser.getPassword());
            log.info("User '{}' successfully registered and authenticated", newUser.getLogin());
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            log.error("Error during registration of user '{}': {}", newUser.getLogin(), e.getMessage(), e);
            return ResponseEntity.badRequest().body("Registration error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for user with login: {}", loginRequest.getLogin());
        try {
            var jwtResponse = authService.authenticateUser(loginRequest.getLogin(), loginRequest.getPassword());
            log.info("User '{}' successfully logged in", loginRequest.getLogin());
            return ResponseEntity.ok(jwtResponse);
        } catch (Exception e) {
            log.error("Error during login of user '{}': {}", loginRequest.getLogin(), e.getMessage(), e);
            return ResponseEntity.status(401).body("Authentication error: " + e.getMessage());
        }
    }
}


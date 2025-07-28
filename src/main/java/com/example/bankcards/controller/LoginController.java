package com.example.bankcards.controller;

import com.example.bankcards.dto.LoginRequest;
import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.RegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final RegisterService registerService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDto newUser)  {

        var registeredUser = registerService.registerUser(newUser);
        var jwtResponse = authService.authenticateUser(newUser.getLogin(), newUser.getPassword());

        return ResponseEntity.ok(jwtResponse);

    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        var jwtResponse = authService.authenticateUser(loginRequest.getLogin(), loginRequest.getPassword());

        return ResponseEntity.ok(jwtResponse);
    }
}

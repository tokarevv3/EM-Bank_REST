package com.example.bankcards.service;

import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.entity.User;

public interface AuthService {

    User getAuthenticatedUser();

    JwtResponse authenticateUser(String login, String password);
}

package com.example.bankcards.service;

import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public User getAuthenticatedUser() {
        var context = SecurityContextHolder.getContext();
        var authentication = context.getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Attempt to get authenticated user without authentication");
            throw new UsernameNotFoundException("User not authenticated");
        }

        var currentUserPrincipal = (UserDetails) authentication.getPrincipal();
        log.debug("Loading user with login: {}", currentUserPrincipal.getUsername());

        return userService.findByLogin(currentUserPrincipal.getUsername())
                .orElseThrow(() -> {
                    log.error("User with login '{}' not found", currentUserPrincipal.getUsername());
                    return new UsernameNotFoundException("User not found");
                });
    }

    public JwtResponse authenticateUser(String login, String password) {
        log.info("Authenticating user with login: {}", login);

        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = tokenProvider.generateToken(authentication);

        log.info("User '{}' successfully authenticated", login);
        return new JwtResponse(token);
    }
}



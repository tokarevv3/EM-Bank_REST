package com.example.bankcards.service;

import com.example.bankcards.dto.JwtResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.JwtTokenProvider;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public User getAuthenticatedUser() {
        var context = SecurityContextHolder.getContext();
        var currentUserPrincipal = (UserDetails) context.getAuthentication().getPrincipal();

        return userService.findByLogin(currentUserPrincipal.getUsername()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public JwtResponse authenticateUser(@NotNull String login, @NotNull String password) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        login,
                        password
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var token = tokenProvider.generateToken(authentication);
        return new JwtResponse(token);
    }
}

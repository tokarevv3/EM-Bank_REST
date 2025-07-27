package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public User getAuthenticatedUser() {
        var context = SecurityContextHolder.getContext();
        var currentUserPrincipal = (UserDetails) context.getAuthentication().getPrincipal();

        return userService.findByLogin(currentUserPrincipal.getUsername()).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

}

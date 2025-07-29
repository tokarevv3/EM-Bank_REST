package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserService userService;

    public void registerUser(UserCreateDto newUserDto) {
        log.info("Registering new user with login: {}", newUserDto.getLogin());
        try {
            userService.createUser(newUserDto);
            log.info("User with login '{}' successfully registered", newUserDto.getLogin());
        } catch (RuntimeException e) {
            log.error("Error registering user with login '{}': {}", newUserDto.getLogin(), e.getMessage(), e);
            throw e;
        }
    }
}


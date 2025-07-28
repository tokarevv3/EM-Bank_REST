package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserService userService;

    public User registerUser(UserCreateDto newUserDto) {
        //TODO::Exception
        return userService.createUser(newUserDto);
    }
}

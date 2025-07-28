package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserService userService;

    public void registerUser(UserCreateDto newUserDto) {
        //TODO::Exception
        userService.createUser(newUserDto);
    }
}

package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;

public interface RegisterService {

    void registerUser(UserCreateDto newUserDto);
}

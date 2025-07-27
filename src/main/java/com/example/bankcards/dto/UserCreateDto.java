package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import lombok.Value;

@Value
public class UserCreateDto {

    String login;

    String password;

    Role role;

}

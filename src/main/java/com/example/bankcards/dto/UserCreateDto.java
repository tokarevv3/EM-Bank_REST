package com.example.bankcards.dto;

import com.example.bankcards.entity.Role;
import com.example.bankcards.exception.validation.annotation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class UserCreateDto {

    @NotBlank(message = "Логин не может быть пустым.")
    String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    String password;

    @EnumValue(enumClass = Role.class, message = "Роль только ADMIN или USER")
    Role role;

}

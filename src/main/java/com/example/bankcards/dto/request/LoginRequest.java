package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class LoginRequest {

    @NotBlank(message = "Логин не может быть пустым.")
    String login;

    @NotBlank(message = "Пароль не может быть пустым.")
    String password;
}

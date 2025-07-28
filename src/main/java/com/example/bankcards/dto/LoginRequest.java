package com.example.bankcards.dto;

import lombok.Value;

@Value
public class LoginRequest {

    String login;

    String password;
}

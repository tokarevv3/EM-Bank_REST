package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserReadMapper implements Mapper<User, UserReadDto> {

    @Override
    public UserReadDto map(User obj) {
        return new UserReadDto(
                obj.getId(),
                obj.getLogin(),
                obj.getRole()
        );
    }
}


package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserReadDto> findAll();

    Optional<UserReadDto> findById(Long id);

    Optional<User> findByLogin(String login);

    Optional<User> getById(Long id);

    void save(User user);

    UserReadDto createUser(UserCreateDto dto);

    boolean deleteUser(Long id);
}

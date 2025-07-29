package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserReadMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserReadMapper userReadMapper;

    public List<UserReadDto> findAll() {
        log.debug("Fetching list of all users");
        return userRepository.findAll().stream()
                .map(userReadMapper::map)
                .toList();
    }

    public Optional<UserReadDto> findById(Long id) {
        log.debug("Searching for user by id: {}", id);
        return userRepository.findById(id)
                .map(userReadMapper::map);
    }

    public Optional<User> findByLogin(String login) {
        log.debug("Searching for user by login: {}", login);
        return userRepository.findByLogin(login);
    }

    public Optional<User> getById(Long id) {
        log.debug("Fetching user entity by id: {}", id);
        return userRepository.findById(id);
    }

    public void save(User user) {
        log.info("Saving user with login: {}", user.getLogin());
        userRepository.save(user);
    }

    public UserReadDto createUser(UserCreateDto dto) {
        log.info("Creating new user with login: {}", dto.getLogin());

        var user = new User();
        user.setLogin(dto.getLogin());
        if (dto.getRole() == null) {
            user.setRole(Role.USER);
            log.debug("User role not specified, defaulting to USER");
        } else {
            user.setRole(dto.getRole());
            log.debug("User role set to: {}", dto.getRole());
        }

        Optional.ofNullable(dto.getPassword())
                .map(passwordEncoder::encode)
                .ifPresentOrElse(
                        user::setPassword,
                        () -> log.warn("Password for user {} is not provided", dto.getLogin())
                );

        UserReadDto createdUserDto = Optional.of(user)
                .map(userRepository::saveAndFlush)
                .map(userReadMapper::map)
                .orElseThrow(() -> {
                    log.error("Failed to save user with login: {}", dto.getLogin());
                    return new RuntimeException("Failed to create user");
                });

        log.info("User with login {} successfully created", dto.getLogin());
        return createdUserDto;
    }

    @Transactional
    public boolean deleteUser(Long id) {
        log.info("Deleting user with id: {}", id);
        return userRepository.findById(id)
                .map(entity -> {
                    userRepository.delete(entity);
                    userRepository.flush();
                    log.info("User with id {} successfully deleted", id);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("User with id {} not found for deletion", id);
                    return false;
                });
    }
}



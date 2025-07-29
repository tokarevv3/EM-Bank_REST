package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class UserController {

    //TODO::create method for update

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserReadDto>> getAllUsers() {
        log.info("Requesting list of all users (admin)");
        var users = userService.findAll();
        log.debug("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserReadDto> getUserById(@PathVariable @Positive(message = "ID must be positive") Long id) {
        log.info("Requesting user by id {} (admin)", id);
        return userService.findById(id)
                .map(user -> {
                    log.debug("User with id {} found", id);
                    return ResponseEntity.ok(user);
                })
                .orElseGet(() -> {
                    log.warn("User with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping
    public ResponseEntity<UserReadDto> createUser(@Valid @RequestBody UserCreateDto dto) {
        log.info("Creating new user with login: {} (admin)", dto.getLogin());
        var createdUser = userService.createUser(dto);
        log.info("User with login {} successfully created", dto.getLogin());
        return ResponseEntity.ok(createdUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive(message = "ID must be positive") Long id) {
        log.info("Deleting user with id {} (admin)", id);
        boolean deleted = userService.deleteUser(id);
        if (deleted) {
            log.info("User with id {} successfully deleted", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("User with id {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
    }
}

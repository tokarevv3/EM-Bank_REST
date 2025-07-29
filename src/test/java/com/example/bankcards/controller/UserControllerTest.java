package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.controller.admin.UserController;
import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = {"ADMIN"})
@Import(SecurityConfiguration.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    

    private UserReadDto sampleUserReadDto;
    private UserCreateDto sampleUserCreateDto;

    @BeforeEach
    void setup() {
        sampleUserReadDto = new UserReadDto(1L, "user1", Role.USER);
        sampleUserCreateDto = new UserCreateDto("user1", "password", Role.USER);
        userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAllUsersShouldReturnList() throws Exception {
        when(userService.findAll()).thenReturn(List.of(sampleUserReadDto));

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(sampleUserReadDto))));

        verify(userService).findAll();
    }

    @Test
    void getUserByIdWhenFoundShouldReturnUser() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.of(sampleUserReadDto));

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleUserReadDto)));

        verify(userService).findById(1L);
    }

    @Test
    void getUserByIdWhenNotFoundShouldReturnNotFound() throws Exception {
        when(userService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).findById(1L);
    }

    @Test
    void getUserByIdWhenIdInvalidShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/users/0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUserShouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(UserCreateDto.class))).thenReturn(sampleUserReadDto);

        mockMvc.perform(post("/api/admin/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleUserCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(sampleUserReadDto)));

        verify(userService).createUser(any(UserCreateDto.class));
    }

    @Test
    void deleteUserWhenDeletedShouldReturnNoContent() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUserWhenNotFoundShouldReturnNotFound() throws Exception {
        when(userService.deleteUser(1L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/users/1"))
                .andExpect(status().isNotFound());

        verify(userService).deleteUser(1L);
    }

    @Test
    void deleteUserWhenIdInvalidShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/admin/users/0"))
                .andExpect(status().isNotFound());
    }
}

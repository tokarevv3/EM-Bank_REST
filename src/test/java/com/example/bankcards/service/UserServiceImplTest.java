package com.example.bankcards.service;

import com.example.bankcards.dto.UserCreateDto;
import com.example.bankcards.dto.UserReadDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.util.mapper.UserReadMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserReadMapper userReadMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private AutoCloseable closeable;

    @BeforeEach
    void setup() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAllShouldReturnListOfUserReadDto() {
        User user1 = new User();
        User user2 = new User();

        UserReadDto dto1 = new UserReadDto();
        UserReadDto dto2 = new UserReadDto();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userReadMapper.map(user1)).thenReturn(dto1);
        when(userReadMapper.map(user2)).thenReturn(dto2);

        List<UserReadDto> result = userService.findAll();

        assertThat(result).containsExactly(dto1, dto2);
    }

    @Test
    void findByIdShouldReturnMappedDtoWhenUserExists() {
        User user = new User();
        user.setId(1L);
        UserReadDto dto = new UserReadDto();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userReadMapper.map(user)).thenReturn(dto);

        Optional<UserReadDto> result = userService.findById(1L);

        assertThat(result).contains(dto);
    }

    @Test
    void findByIdShouldReturnEmptyWhenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<UserReadDto> result = userService.findById(1L);

        assertThat(result).isEmpty();
    }

    @Test
    void findByLoginShouldReturnUserWhenExists() {
        User user = new User();
        user.setLogin("user");

        when(userRepository.findByLogin("user")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByLogin("user");

        assertThat(result).contains(user);
    }

    @Test
    void getByIdShouldReturnUserWhenExists() {
        User user = new User();
        user.setId(5L);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getById(5L);

        assertThat(result).contains(user);
    }

    @Test
    void saveShouldSaveUser() {
        User user = new User();

        userService.save(user);

        verify(userRepository).save(user);
    }

    @Test
    void createUserShouldCreateWithDefaultRoleAndEncodePassword() {
        UserCreateDto dto = new UserCreateDto("newuser", "plainpass", null);

        User savedUser = new User();
        savedUser.setLogin("newuser");
        savedUser.setRole(Role.USER);
        savedUser.setPassword("encodedpass");

        UserReadDto dtoResult = new UserReadDto();

        when(passwordEncoder.encode("plainpass")).thenReturn("encodedpass");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(savedUser);
        when(userReadMapper.map(savedUser)).thenReturn(dtoResult);

        UserReadDto result = userService.createUser(dto);

        assertThat(result).isEqualTo(dtoResult);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());

        User userToSave = userCaptor.getValue();
        assertThat(userToSave.getRole()).isEqualTo(Role.USER);
        assertThat(userToSave.getPassword()).isEqualTo("encodedpass");
    }

    @Test
    void createUserShouldRespectExplicitRoleAndNullPassword() {
        UserCreateDto dto = new UserCreateDto("admin", null, Role.ADMIN);

        User user = new User();
        user.setLogin("admin");
        user.setRole(Role.ADMIN);

        UserReadDto readDto = new UserReadDto();

        when(userRepository.saveAndFlush(any())).thenReturn(user);
        when(userReadMapper.map(user)).thenReturn(readDto);

        UserReadDto result = userService.createUser(dto);

        assertThat(result).isEqualTo(readDto);

        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void deleteUserShouldDeleteAndReturnTrueWhenExists() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(1L);

        assertThat(result).isTrue();
        verify(userRepository).delete(user);
        verify(userRepository).flush();
    }

    @Test
    void deleteUserShouldReturnFalseWhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = userService.deleteUser(999L);

        assertThat(result).isFalse();
        verify(userRepository, never()).delete(any());
    }
}

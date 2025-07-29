package com.example.bankcards.controller;

import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.controller.user.UserCardController;
import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.security.CardOwnershipValidator;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.CardService;
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
import org.springframework.data.domain.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = {"USER"})
@Import(SecurityConfiguration.class)
@ExtendWith(MockitoExtension.class)
class UserCardControllerTest {

    @Mock
    private CardService cardService;

    @Mock
    private AuthService authService;

    @Mock
    private CardOwnershipValidator cardOwnershipValidator;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);



    @InjectMocks
    private UserCardController userCardController;


    private CardReadDto sampleCard;
    private Pageable pageable;
    private Page<CardReadDto> page;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "login", "password", Role.USER, null);
        sampleCard = new CardReadDto(1L, "**** **** **** 1234", LocalDate.now(),Status.ACTIVE, BigDecimal.valueOf(1000));
        pageable = PageRequest.of(0, 10, Sort.unsorted());
        page = new PageImpl<>(List.of(sampleCard), pageable, 1);

        mockMvc = MockMvcBuilders.standaloneSetup(userCardController).build();

//        when(authService.getAuthenticatedUser()).thenReturn(user);
    }

    @Test
    void findUserCardsShouldReturnPage() throws Exception {
        when(cardService.findAllByUserAndFilter(eq(user), any(CardFilter.class), any(Pageable.class)))
                .thenReturn(page);
        when(authService.getAuthenticatedUser()).thenReturn(user);

        mockMvc.perform(get("/api/user/cards")
                        .param("page", "0")
                        .param("size", "10")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(sampleCard.getId()))
                .andExpect(jsonPath("$.content[0].maskedNumber").value(sampleCard.getMaskedNumber()))
                .andExpect(jsonPath("$.content[0].status").value(sampleCard.getStatus().toString()));

        verify(authService).getAuthenticatedUser();
        verify(cardService).findAllByUserAndFilter(eq(user), any(CardFilter.class), any(Pageable.class));
    }

    @Test
    void blockCardWhenValidShouldReturnTrue() throws Exception {
        doNothing().when(cardOwnershipValidator).validateOwnership(1L);
        when(cardService.updateCardStatus(1L, Status.BLOCKED)).thenReturn(true);

        mockMvc.perform(patch("/api/user/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(cardOwnershipValidator).validateOwnership(1L);
        verify(cardService).updateCardStatus(1L, Status.BLOCKED);
    }
    

    @Test
    void getCardBalanceWhenFoundShouldReturnBalance() throws Exception {
        doNothing().when(cardOwnershipValidator).validateOwnership(1L);
        when(cardService.findById(1L)).thenReturn(Optional.of(sampleCard));

        mockMvc.perform(get("/api/user/cards/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string(sampleCard.getBalance().toString()));

        verify(cardOwnershipValidator).validateOwnership(1L);
        verify(cardService).findById(1L);
    }

    @Test
    void getCardBalanceWhenCardNotFoundShouldReturnNotFound() throws Exception {
        doNothing().when(cardOwnershipValidator).validateOwnership(1L);
        when(cardService.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/user/cards/1/balance"))
                .andExpect(status().isNotFound());

        verify(cardOwnershipValidator).validateOwnership(1L);
        verify(cardService).findById(1L);
    }

    @Test
    void getCardBalanceWhenIdInvalidShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/user/cards/-1/balance"))
                .andExpect(status().isNotFound());
    }
}


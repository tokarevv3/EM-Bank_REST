package com.example.bankcards.controller;

import com.example.bankcards.BankRestApplicationTest;
import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.controller.admin.CardController;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.request.CardStatusUpdateRequest;
import com.example.bankcards.entity.Status;
import com.example.bankcards.service.CardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WithMockUser(roles = {"ADMIN"})
@Import(SecurityConfiguration.class)
@ExtendWith(MockitoExtension.class)
public class CardControllerTest {

//    @Autowired
    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        cardController = new CardController(cardService);
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).build();
    }


    @Test
    void getAllCardsShouldReturnListOfCards() throws Exception {
        var cards = List.of(
                new CardReadDto(1L, null, null, Status.ACTIVE, null),
                new CardReadDto(2L, null, null, Status.BLOCKED, null)
        );
        Mockito.when(cardService.findAll()).thenReturn(cards);

        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(cards)));
    }

    @Test
    void getCardExistingIdShouldReturnCard() throws Exception {
        var card = new CardReadDto(1L, null, null, Status.ACTIVE, null);
        Mockito.when(cardService.findById(1L)).thenReturn(Optional.of(card));

        mockMvc.perform(get("/api/admin/cards/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(card)));
    }

    @Test
    void getCardNonExistingIdShouldReturnNotFound() throws Exception {
        Mockito.when(cardService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/cards/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCardInvalidIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/admin/cards/-1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCardValidIdShouldReturnCreatedCard() throws Exception {
        var createdCard = new CardReadDto(10L, null, null, Status.ACTIVE, null);
        Mockito.when(cardService.createCard(5L)).thenReturn(createdCard);

        mockMvc.perform(post("/api/admin/cards/5"))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(createdCard)));
    }

    @Test
    void updateCardExistingIdValidDtoShouldReturnOk() throws Exception {
        var dto = new CardStatusUpdateRequest(Status.ACTIVE);

        Mockito.when(cardService.updateCardStatus(1L, Status.ACTIVE)).thenReturn(true);

        mockMvc.perform(patch("/api/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void updateCardNonExistingIdShouldReturnNotFound() throws Exception {
        var dto = new CardStatusUpdateRequest(Status.ACTIVE);

        Mockito.when(cardService.updateCardStatus(999L, Status.ACTIVE)).thenReturn(false);

        mockMvc.perform(patch("/api/admin/cards/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCardInvalidIdShouldReturnNotFound() throws Exception {
        var dto = new CardStatusUpdateRequest(Status.ACTIVE);

        mockMvc.perform(patch("/api/admin/cards/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCardInvalidStatusShouldReturnBadRequest() throws Exception {
        var dto = new CardStatusUpdateRequest(Status.EXPIRED);

        mockMvc.perform(patch("/api/admin/cards/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCardExistingIdShouldReturnNoContent() throws Exception {
        Mockito.when(cardService.deleteCard(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/cards/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCardNonExistingIdShouldReturnNotFound() throws Exception {
        Mockito.when(cardService.deleteCard(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/cards/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteCardInvalidIdShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/admin/cards/0"))
                .andExpect(status().isNotFound());
    }
}


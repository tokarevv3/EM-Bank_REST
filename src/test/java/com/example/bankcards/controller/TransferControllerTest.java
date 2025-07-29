package com.example.bankcards.controller;


import com.example.bankcards.config.SecurityConfiguration;
import com.example.bankcards.controller.user.TransferController;
import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.security.CardOwnershipValidator;
import com.example.bankcards.service.TransferService;
import com.example.bankcards.util.validation.handler.GlobalExceptionHandler;
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

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(roles = "USER")
@Import({SecurityConfiguration.class, GlobalExceptionHandler.class})
@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferService transferService;
    
    @InjectMocks
    private TransferController transferController;

    @Mock
    private CardOwnershipValidator cardOwnershipValidator;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(transferController).build();
    }

    @Test
    void transferAmountWhenValidRequestShouldReturnNoContent() throws Exception {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/api/user/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(cardOwnershipValidator).validateOwnership(1L);
        verify(cardOwnershipValidator).validateOwnership(2L);
        verify(transferService).transferAmount(request);
    }

    @Test
    void transferAmountWhenValidationFailsShouldReturnBadRequest() throws Exception {
        TransferRequest request = new TransferRequest(null, 2L, BigDecimal.valueOf(100.0));

        mockMvc.perform(post("/api/user/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(cardOwnershipValidator);
        verifyNoInteractions(transferService);
    }
}


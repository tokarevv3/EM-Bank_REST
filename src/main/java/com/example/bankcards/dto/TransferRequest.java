package com.example.bankcards.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class TransferRequest {
    @NotNull
    Long fromCardId;

    @NotNull
    Long toCardId;

    @NotNull
    @Positive
    BigDecimal amount;
}

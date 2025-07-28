package com.example.bankcards.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Value;

import java.math.BigDecimal;

@Value
public class TransferRequest {
    @NotNull
    @Positive
    Long fromCardId;

    @NotNull
    @Positive
    Long toCardId;

    @NotNull
    @Positive
    BigDecimal amount;
}

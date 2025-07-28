package com.example.bankcards.dto;

import com.example.bankcards.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CardReadDto {

    Long id;
    String maskedNumber;
    LocalDate expiredDate;
    Status status;
    BigDecimal balance;
}

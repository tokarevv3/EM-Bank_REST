package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.EncryptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardReadMapper implements Mapper<Card, CardReadDto> {

    private final EncryptionService encryptionService;

    @Override
    public CardReadDto map(Card obj) {

        String decryptedNumber = encryptionService.decrypt(obj.getEncryptedNumber());
        String maskedNumber = maskCardNumber(decryptedNumber);

        return new CardReadDto(
                obj.getId(),
                maskedNumber,
                obj.getExpiredDate(),
                obj.getStatus(),
                obj.getBalance()
        );
    }

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}

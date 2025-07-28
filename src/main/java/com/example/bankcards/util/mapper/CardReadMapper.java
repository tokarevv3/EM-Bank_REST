package com.example.bankcards.util.mapper;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Card;
import org.springframework.stereotype.Component;

@Component
public class CardReadMapper implements Mapper<Card, CardReadDto> {

    @Override
    public CardReadDto map(Card obj) {
        return new CardReadDto(
                obj.getId(),
                obj.getEncryptedNumber(),
                obj.getExpiredDate(),
                obj.getStatus(),
                obj.getBalance()
        );
    }
}

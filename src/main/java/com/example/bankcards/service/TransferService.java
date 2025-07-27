package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.InactiveCardException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardService cardService;

    @Transactional
    public void transferAmount(Long fromCardId, Long toCardId, BigDecimal amount) {
        Card fromCard = cardService.findById(fromCardId)
                .orElseThrow(() -> new IllegalArgumentException("Sender card not found"));

        Card toCard = cardService.findById(toCardId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver card not found"));

        validateCardIsActive(fromCard);
        validateCardIsActive(toCard);

        fromCard.withdraw(amount);
        toCard.deposit(amount);

    }

    private void validateCardIsActive(Card card) {
        if (!card.isActive()) {
            throw new InactiveCardException("Card " + card.getId() + " is not active");
        }
    }
}


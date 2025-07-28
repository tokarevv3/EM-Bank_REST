package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.InactiveCardException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final CardService cardService;

    @Transactional
    public void transferAmount(TransferRequest request) {
        Card fromCard = cardService.getById(request.getFromCardId())
                .orElseThrow(() -> new IllegalArgumentException("Sender card not found"));

        Card toCard = cardService.getById(request.getToCardId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver card not found"));

        validateCardIsActive(fromCard);
        validateCardIsActive(toCard);

        fromCard.withdraw(request.getAmount());
        toCard.deposit(request.getAmount());

    }

    private void validateCardIsActive(Card card) {
        if (!card.isActive()) {
            throw new InactiveCardException("Card " + card.getId() + " is not active");
        }
    }
}


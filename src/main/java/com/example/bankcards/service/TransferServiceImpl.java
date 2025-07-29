package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.InactiveCardException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransferServiceImpl implements TransferService {

    private final CardService cardService;

    @Transactional
    public void transferAmount(TransferRequest request) {
        log.info("Starting fund transfer: fromCardId={}, toCardId={}, amount={}",
                request.getFromCardId(), request.getToCardId(), request.getAmount());

        if (Objects.equals(request.getFromCardId(), request.getToCardId())) {
            log.error("Error: Attempt to transfer between the same card (id={})", request.getFromCardId());
            throw new IllegalArgumentException("Cannot transfer between same card.");
        }

        Card fromCard = cardService.getById(request.getFromCardId())
                .orElseThrow(() -> {
                    log.error("Sender card with id {} not found", request.getFromCardId());
                    return new IllegalArgumentException("Sender card not found");
                });

        Card toCard = cardService.getById(request.getToCardId())
                .orElseThrow(() -> {
                    log.error("Receiver card with id {} not found", request.getToCardId());
                    return new IllegalArgumentException("Receiver card not found");
                });

        validateCardIsActive(fromCard);
        validateCardIsActive(toCard);

        fromCard.withdraw(request.getAmount());
        toCard.deposit(request.getAmount());

        log.info("Fund transfer successfully completed: {} from card {} to card {}",
                request.getAmount(), fromCard.getId(), toCard.getId());
    }

    private void validateCardIsActive(Card card) {
        if (!card.isActive()) {
            log.error("Card with id {} is inactive", card.getId());
            throw new InactiveCardException("Card " + card.getId() + " is not active");
        }
        log.debug("Card with id {} is active", card.getId());
    }
}

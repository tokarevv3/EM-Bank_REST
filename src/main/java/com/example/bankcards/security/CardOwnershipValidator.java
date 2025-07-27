package com.example.bankcards.security;

import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardOwnershipValidator {

    private final CardService cardService;

    public void validateOwnership(Long cardId) {
        Card card = cardService.findById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + "not found."));
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!card.getOwner().getLogin().equals(currentUsername)) {
            throw new AccessDeniedException("You do not have access to this card");
        }
    }
}

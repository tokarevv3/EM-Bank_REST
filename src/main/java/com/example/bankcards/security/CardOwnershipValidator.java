package com.example.bankcards.security;

import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CardOwnershipValidator {

    private final CardService cardService;
    private final AuthService authService;

    public void validateOwnership(Long cardId) {
        var card = cardService.getById(cardId)
                .orElseThrow(() -> new CardNotFoundException("Card with ID " + cardId + "not found."));
        String currentUserLogin = authService.getAuthenticatedUser().getLogin();

        if (!card.getOwner().getLogin().equals(currentUserLogin)) {
            throw new AccessDeniedException("You do not have access to this card");
        }
    }
}

package com.example.bankcards.controller.user;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Status;
import com.example.bankcards.security.CardOwnershipValidator;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.CardService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('USER')")
public class UserCardController {

    private final CardService cardService;
    private final AuthService authService;
    private final CardOwnershipValidator cardOwnershipValidator;

    @GetMapping("/cards")
    public ResponseEntity<Page<CardReadDto>> findUserCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status
    ) {
        var currentUser = authService.getAuthenticatedUser();
        log.info("User '{}' requests cards, page: {}, size: {}, status filter: {}",
                currentUser.getLogin(), page, size, status);

        Pageable pageable = PageRequest.of(page, size);
        CardFilter filter = new CardFilter(status);

        Page<CardReadDto> result = cardService.findAllByUserAndFilter(currentUser, filter, pageable);
        log.debug("Found {} cards for user '{}'", result.getTotalElements(), currentUser.getLogin());
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/cards/{id}")
    public ResponseEntity<Boolean> blockCard(@PathVariable @Positive(message = "ID must be positive") Long id)
            throws IllegalAccessException {
        log.info("User attempts to block card with id: {}", id);
        cardOwnershipValidator.validateOwnership(id);

        boolean result = cardService.updateCardStatus(id, Status.BLOCKED);
        log.info("Blocking card with id {} was {}", id, result ? "successful" : "unsuccessful");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/cards/{id}/balance")
    public ResponseEntity<BigDecimal> getCardBalance(@PathVariable @Positive(message = "ID must be positive") Long id) {
        log.info("Requesting balance for card with id: {}", id);
        cardOwnershipValidator.validateOwnership(id);

        var card = cardService.findById(id)
                .orElseThrow(() -> {
                    log.warn("Card with id {} not found", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found");
                });

        log.debug("Balance for card id {}: {}", id, card.getBalance());
        return ResponseEntity.ok(card.getBalance());
    }
}

package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.request.CardStatusUpdateRequest;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardReadDto>> getAllCards() {
        log.info("All card list request (admin)");
        var cards = cardService.findAll();
        log.debug("Found {} cards", cards.size());
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardReadDto> getCard(@PathVariable @Positive(message = "ID должен быть положительным") Long id) {
        log.info("Card request by id {} (admin)", id);
        return cardService.findById(id)
                .map(card -> {
                    log.debug("Found card with id {}", id);
                    return ResponseEntity.ok(card);
                })
                .orElseGet(() -> {
                    log.warn("Cannot find card with id {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping("/{id}")
    public ResponseEntity<CardReadDto> createCard(@PathVariable @Positive(message = "ID пользователя должен быть положительным") Long id) {
        log.info("Create card for user with id {} (admin)", id);
        CardReadDto createdCard = cardService.createCard(id);
        log.info("Successfully created card for user with id {}", id);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCard);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCard(@PathVariable @Positive(message = "ID должен быть положительным") Long id,
                                           @Valid @RequestBody CardStatusUpdateRequest dto) throws IllegalAccessException {
        log.info("Update status for card {} to {}", id, dto.getStatus());
        boolean updated = cardService.updateCardStatus(id, dto.getStatus());

        if (updated) {
            log.info("Status for card with {} successfully updated", id);
            return ResponseEntity.ok().build();
        } else {
            log.warn("Cannot find card with id {} for status update", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable @Positive(message = "ID должен быть положительным") Long id) {
        log.info("Delete card with id {} (admin)", id);
        boolean deleted = cardService.deleteCard(id);

        if (deleted) {
            log.info("Successfully deleted card with id {}", id);
            return ResponseEntity.noContent().build();
        } else {
            log.warn("Cannot find card with id {} for delete", id);
            return ResponseEntity.notFound().build();
        }
    }
}

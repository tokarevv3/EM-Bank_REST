package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.dto.request.CardStatusUpdateRequest;
import com.example.bankcards.service.CardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Validated
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<CardReadDto>> getAllCards() {
        return ResponseEntity.ok(cardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardReadDto> getCard(@PathVariable @Positive(message = "ID должен быть положительным") Long id) {
        return cardService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CardReadDto> createCard(@PathVariable @Positive(message = "ID пользователя должен быть положительным") Long id) {
        return ResponseEntity.ok(cardService.createCard(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCard(@PathVariable @Positive(message = "ID должен быть положительным") Long id,
                              @Valid @RequestBody CardStatusUpdateRequest dto) throws IllegalAccessException {

        var updated = cardService.updateCardStatus(id, dto.getStatus());
        return updated
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable @Positive(message = "ID должен быть положительным") Long id) {
        boolean deleted = cardService.deleteCard(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

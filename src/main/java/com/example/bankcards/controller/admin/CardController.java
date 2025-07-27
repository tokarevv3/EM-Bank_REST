package com.example.bankcards.controller.admin;

import com.example.bankcards.dto.CardStatusUpdateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @GetMapping
    public ResponseEntity<List<Card>> getAllCards() {
        return ResponseEntity.ok(cardService.findAll());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Card createCard(@RequestParam Long userId) {
        return cardService.createCard(userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateCard(@PathVariable Long id,
                              @RequestBody CardStatusUpdateDto dto) throws IllegalAccessException {

        if (!dto.isAllowedForUpdate()) {
            return ResponseEntity.badRequest().build();
        }

        var updated = cardService.updateCardStatus(id, dto.getStatus());
        return updated
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        boolean deleted = cardService.deleteCard(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

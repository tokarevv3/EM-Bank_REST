package com.example.bankcards.controller.user;

import com.example.bankcards.dto.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.security.CardOwnershipValidator;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;
    private final AuthService authService;
    private final TransferService transferService;
    private final CardOwnershipValidator cardOwnershipValidator;


    //TODO:: PAGE AND FILTER
    @GetMapping("/cards")
    public List<Card> findUserCards() {

        var currentUser = authService.getAuthenticatedUser();

        return cardService.findByUser(currentUser);
    }

    @PatchMapping("/cards/{id}")
    public boolean blockCard(@PathVariable Long id) throws IllegalAccessException {
        cardOwnershipValidator.validateOwnership(id);
        return cardService.updateCardStatus(id, Status.BLOCKED);
    }

    @GetMapping("/cards/{id}/balance")
    public BigDecimal getCardBalance(@PathVariable Long id) {
        cardOwnershipValidator.validateOwnership(id);
        return cardService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Card not found"))
                .getBalance();
    }

    @PostMapping("/transfer")
    public void transferAmount(@Valid @RequestBody TransferRequest request) {
        cardOwnershipValidator.validateOwnership(request.getFromCardId());
        cardOwnershipValidator.validateOwnership(request.getToCardId());
        transferService.transferAmount(request);
    }
}

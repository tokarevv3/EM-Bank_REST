package com.example.bankcards.controller.user;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.security.CardOwnershipValidator;
import com.example.bankcards.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class TransferController {

    private final TransferService transferService;
    private final CardOwnershipValidator cardOwnershipValidator;

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferAmount(@Valid @RequestBody TransferRequest request) {
        log.info("User initiates transfer: fromCardId={}, toCardId={}, amount={}",
                request.getFromCardId(), request.getToCardId(), request.getAmount());

        cardOwnershipValidator.validateOwnership(request.getFromCardId());
        cardOwnershipValidator.validateOwnership(request.getToCardId());

        transferService.transferAmount(request);

        log.info("Transfer completed successfully: fromCardId={}, toCardId={}, amount={}",
                request.getFromCardId(), request.getToCardId(), request.getAmount());

        return ResponseEntity.noContent().build();
    }
}




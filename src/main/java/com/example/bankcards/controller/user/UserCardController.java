package com.example.bankcards.controller.user;

import com.example.bankcards.entity.Card;
import com.example.bankcards.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserCardController {

    private final CardService cardService;

    @GetMapping("/cards")
    public List<Card> findUserCards() {
        cardService.findBy
    }
}

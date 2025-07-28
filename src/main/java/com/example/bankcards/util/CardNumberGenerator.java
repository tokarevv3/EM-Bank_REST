package com.example.bankcards.util;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CardNumberGenerator {

    public String generateCardNumber() {
        Random rand = new Random();
        StringBuilder cardNumber = new StringBuilder();

        for (int i = 0; i < 16; i++) {
            cardNumber.append(rand.nextInt(10));
        }

        return cardNumber.toString();
    }
}

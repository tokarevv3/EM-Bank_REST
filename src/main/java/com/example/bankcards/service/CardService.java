package com.example.bankcards.service;

import com.example.bankcards.dto.CardCreateDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;

    public Card createCard(Long userId) {
        return null;
    }

    public Optional<Card> findById(Long id) {
        return cardRepository.findById(id);
    }

    public List<Card> findByUserId(Long id) {
        return cardRepository.findAllByOwner();
    }

    public List<Card> findAll() {
        return cardRepository.findAll();
    }

    public boolean deleteCard(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    cardRepository.delete(entity);
                    cardRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    public boolean blockCard(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    entity.setStatus(Status.BLOCKED);
                    cardRepository.saveAndFlush(entity);
                    return true;
                })
                .orElse(false);
    }

    public boolean activeCard(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    entity.setStatus(Status.ACTIVE);
                    cardRepository.saveAndFlush(entity);
                    return true;
                })
                .orElse(false);
    }

    public boolean updateCardStatus(Long id, Status newStatus) throws IllegalAccessException {

        if (newStatus == Status.EXPIRED) {
            throw new IllegalAccessException("EXPIRED status cannot be set manually.");
        }

        return cardRepository.findById(id)
                .map(entity -> {

                    if (entity.getStatus().equals(newStatus)) return true;

                    entity.setStatus(newStatus);
                    cardRepository.saveAndFlush(entity);
                    return true;
                })
                .orElse(false);
    }
}

package com.example.bankcards.service;

import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mapper.CardReadMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardNumberGenerator cardNumberGenerator;
    private final CardReadMapper cardReadMapper;

    @Transactional
    public CardReadDto createCard(Long userId) {
        User user = userService.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        Card card = new Card();
        card.setOwner(user);
        card.setEncryptedNumber(cardNumberGenerator.generateCardNumber());
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(Status.ACTIVE);
        card.setExpiredDate(LocalDate.now().plusYears(5));

        user.getCardList().add(card);
        userService.save(user);

        return Optional.of(cardRepository.save(card))
                .map(cardReadMapper::map)
                .orElseThrow();
    }

    public Optional<CardReadDto> findById(Long id) {
        return cardRepository.findById(id)
                .map(cardReadMapper::map);
    }

    public Optional<Card> getById(Long id) {
        return cardRepository.findById(id);
    }

    public List<CardReadDto> findByUser(User user) {
        return cardRepository.findAllByOwner(user).stream()
                .map(cardReadMapper::map)
                .toList();
    }

    public List<CardReadDto> findAll() {
        return cardRepository.findAll().stream()
                .map(cardReadMapper::map)
                .toList();
    }

    @Transactional
    public boolean deleteCard(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    cardRepository.delete(entity);
                    cardRepository.flush();
                    return true;
                })
                .orElse(false);
    }

    @Deprecated
    @Transactional
    public boolean blockCard(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    entity.setStatus(Status.BLOCKED);
                    cardRepository.saveAndFlush(entity);
                    return true;
                })
                .orElse(false);
    }

    @Deprecated
    @Transactional
    public boolean activeCard(Long id) {
        return cardRepository.findById(id)
                .map(entity -> {
                    entity.setStatus(Status.ACTIVE);
                    cardRepository.saveAndFlush(entity);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
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

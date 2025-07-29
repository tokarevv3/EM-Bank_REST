package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.CardNumberGenerator;
import com.example.bankcards.util.mapper.CardReadMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final EncryptionService encryptionService;
    private final CardNumberGenerator cardNumberGenerator;
    private final CardReadMapper cardReadMapper;

    @Transactional
    public CardReadDto createCard(Long userId) {
        log.info("Creating card for user with id: {}", userId);
        User user = userService.getById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found when creating card", userId);
                    return new EntityNotFoundException("User with id " + userId + " not found");
                });

        Card card = new Card();
        card.setOwner(user);
        card.setEncryptedNumber(encryptionService.encrypt(cardNumberGenerator.generateCardNumber()));
        card.setBalance(BigDecimal.ZERO);
        card.setStatus(Status.ACTIVE);
        card.setExpiredDate(LocalDate.now().plusYears(5));

        user.getCardList().add(card);
        userService.save(user);
        log.debug("Card created with encrypted number: {}", card.getEncryptedNumber());

        return Optional.of(cardRepository.save(card))
                .map(cardReadMapper::map)
                .orElseThrow(() -> {
                    log.error("Failed to save card for user with id: {}", userId);
                    return new RuntimeException("Failed to save card");
                });
    }

    public Optional<CardReadDto> findById(Long id) {
        log.debug("Getting card by id: {}", id);
        return cardRepository.findById(id)
                .map(cardReadMapper::map);
    }

    public Optional<Card> getById(Long id) {
        log.debug("Getting card entity by id: {}", id);
        return cardRepository.findById(id);
    }

    public List<CardReadDto> findAllByUser(User user) {
        log.debug("Getting all cards for user with id: {}", user.getId());
        return cardRepository.findAllByOwner(user).stream()
                .map(cardReadMapper::map)
                .toList();
    }

    public List<CardReadDto> findAll() {
        log.debug("Getting all cards");
        return cardRepository.findAll().stream()
                .map(cardReadMapper::map)
                .toList();
    }

    public Page<CardReadDto> findAllByUserAndFilter(User user, CardFilter filter, Pageable pageable) {
        log.debug("Getting paged cards for user with id: {} with filter: {}", user.getId(), filter);
        return cardRepository.findByUserWithFilter(user, filter, pageable)
                .map(cardReadMapper::map);
    }

    @Transactional
    public boolean deleteCard(Long id) {
        log.info("Deleting card with id: {}", id);
        return cardRepository.findById(id)
                .map(entity -> {
                    cardRepository.delete(entity);
                    cardRepository.flush();
                    log.info("Card with id {} successfully deleted", id);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Card with id {} not found for deletion", id);
                    return false;
                });
    }

    @Deprecated
    @Transactional
    public boolean blockCard(Long id) {
        log.info("Blocking card with id: {}", id);
        return cardRepository.findById(id)
                .map(entity -> {
                    entity.setStatus(Status.BLOCKED);
                    cardRepository.saveAndFlush(entity);
                    log.info("Card with id {} blocked", id);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Card with id {} not found for blocking", id);
                    return false;
                });
    }

    @Deprecated
    @Transactional
    public boolean activeCard(Long id) {
        log.info("Activating card with id: {}", id);
        return cardRepository.findById(id)
                .map(entity -> {
                    entity.setStatus(Status.ACTIVE);
                    cardRepository.saveAndFlush(entity);
                    log.info("Card with id {} activated", id);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Card with id {} not found for activation", id);
                    return false;
                });
    }

    @Transactional
    public boolean updateCardStatus(Long id, Status newStatus) throws IllegalAccessException {
        log.info("Updating card status for id: {} to status: {}", id, newStatus);

        if (newStatus == Status.EXPIRED) {
            log.error("Attempt to manually set EXPIRED status for card with id: {}", id);
            throw new IllegalAccessException("EXPIRED status cannot be set manually.");
        }

        return cardRepository.findById(id)
                .map(entity -> {
                    if (entity.getStatus().equals(newStatus)) {
                        log.info("Card status for id {} is already {}", id, newStatus);
                        return true;
                    }

                    entity.setStatus(newStatus);
                    cardRepository.saveAndFlush(entity);
                    log.info("Card status for id {} updated to {}", id, newStatus);
                    return true;
                })
                .orElseGet(() -> {
                    log.warn("Card with id {} not found for status update", id);
                    return false;
                });
    }

    public String getMaskedCardNumber(Card card) {
        String decrypted = encryptionService.decrypt(card.getEncryptedNumber());
        return "**** **** **** " + decrypted.substring(decrypted.length() - 4);
    }
}



package com.example.bankcards.service;

import com.example.bankcards.dto.CardFilter;
import com.example.bankcards.dto.CardReadDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.Status;
import com.example.bankcards.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CardService {

    CardReadDto createCard(Long userId);

    Optional<CardReadDto> findById(Long id);

    Optional<Card> getById(Long id);

    List<CardReadDto> findAllByUser(User user);

    List<CardReadDto> findAll();

    boolean deleteCard(Long id);

    boolean updateCardStatus(Long id, Status newStatus) throws IllegalAccessException;

    Page<CardReadDto> findAllByUserAndFilter(User user, CardFilter filter, Pageable pageable);
}

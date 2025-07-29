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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserService userService;
    @Mock
    private EncryptionService encryptionService;
    @Mock
    private CardNumberGenerator cardNumberGenerator;
    @Mock
    private CardReadMapper cardReadMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCardShouldReturnCardReadDtoWhenUserExists() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setCardList(new ArrayList<>());

        Card card = new Card();
        card.setId(10L);
        card.setOwner(user);

        CardReadDto dto = new CardReadDto(10L, "123", LocalDate.now(), Status.ACTIVE, BigDecimal.ZERO);

        when(userService.getById(userId)).thenReturn(Optional.of(user));
        when(cardNumberGenerator.generateCardNumber()).thenReturn("123");
        when(cardRepository.save(any())).thenReturn(card);
        when(cardReadMapper.map(card)).thenReturn(dto);

        CardReadDto result = cardService.createCard(userId);

        assertThat(result).isEqualTo(dto);
        verify(userService).save(user);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCardShouldThrowExceptionWhenUserNotFound() {
        when(userService.getById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User with id");
    }

    @Test
    void findByIdShouldReturnDtoWhenCardExists() {
        Card card = new Card();
        card.setId(1L);
        CardReadDto dto = new CardReadDto(1L, "123", LocalDate.now(), Status.ACTIVE, BigDecimal.ZERO);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardReadMapper.map(card)).thenReturn(dto);

        Optional<CardReadDto> result = cardService.findById(1L);

        assertThat(result).contains(dto);
    }

    @Test
    void getByIdShouldReturnEntityWhenExists() {
        Card card = new Card();
        when(cardRepository.findById(2L)).thenReturn(Optional.of(card));

        Optional<Card> result = cardService.getById(2L);

        assertThat(result).contains(card);
    }

    @Test
    void findAllByUserShouldReturnMappedList() {
        User user = new User();
        List<Card> cards = List.of(new Card(), new Card());
        List<CardReadDto> dtos = List.of(new CardReadDto(), new CardReadDto());

        when(cardRepository.findAllByOwner(user)).thenReturn(cards);
        when(cardReadMapper.map(any())).thenReturn(new CardReadDto());

        List<CardReadDto> result = cardService.findAllByUser(user);

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllShouldReturnAllCards() {
        List<Card> cards = List.of(new Card(), new Card());
        when(cardRepository.findAll()).thenReturn(cards);
        when(cardReadMapper.map(any())).thenReturn(new CardReadDto());

        List<CardReadDto> result = cardService.findAll();

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllByUserAndFilterShouldReturnPage() {
        User user = new User();
        Pageable pageable = PageRequest.of(0, 2);
        CardFilter filter = new CardFilter("ACTIVE");

        Page<Card> page = new PageImpl<>(List.of(new Card(), new Card()));
        when(cardRepository.findByUserWithFilter(user, filter, pageable)).thenReturn(page);
        when(cardReadMapper.map(any())).thenReturn(new CardReadDto());

        Page<CardReadDto> result = cardService.findAllByUserAndFilter(user, filter, pageable);

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void deleteCardShouldDeleteAndReturnTrueWhenFound() {
        Card card = new Card();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        boolean result = cardService.deleteCard(1L);

        assertThat(result).isTrue();
        verify(cardRepository).delete(card);
        verify(cardRepository).flush();
    }

    @Test
    void deleteCardShouldReturnFalseWhenNotFound() {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());

        boolean result = cardService.deleteCard(999L);

        assertThat(result).isFalse();
    }

    @Test
    void updateCardStatusShouldUpdateStatusWhenValid() throws IllegalAccessException {
        Card card = new Card();
        card.setStatus(Status.ACTIVE);

        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        boolean result = cardService.updateCardStatus(1L, Status.BLOCKED);

        assertThat(result).isTrue();
        assertThat(card.getStatus()).isEqualTo(Status.BLOCKED);
        verify(cardRepository).saveAndFlush(card);
    }

    @Test
    void updateCardStatusShouldThrowWhenSettingExpired() {
        assertThatThrownBy(() -> cardService.updateCardStatus(1L, Status.EXPIRED))
                .isInstanceOf(IllegalAccessException.class)
                .hasMessageContaining("EXPIRED status cannot be set manually");
    }

    @Test
    void updateCardStatusShouldReturnFalseWhenNotFound() throws IllegalAccessException {
        when(cardRepository.findById(any())).thenReturn(Optional.empty());

        boolean result = cardService.updateCardStatus(99L, Status.BLOCKED);

        assertThat(result).isFalse();
    }
}


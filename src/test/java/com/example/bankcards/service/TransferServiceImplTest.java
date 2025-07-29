package com.example.bankcards.service;

import com.example.bankcards.dto.request.TransferRequest;
import com.example.bankcards.entity.Card;
import com.example.bankcards.exception.InactiveCardException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransferServiceImplTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private TransferServiceImpl transferService;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @Test
    void transferAmountShouldTransferWhenCardsExistAndActive() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"));

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);

        when(cardService.getById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getById(2L)).thenReturn(Optional.of(toCard));
        when(fromCard.isActive()).thenReturn(true);
        when(toCard.isActive()).thenReturn(true);

        transferService.transferAmount(request);

        verify(fromCard).withdraw(request.getAmount());
        verify(toCard).deposit(request.getAmount());
    }

    @Test
    void transferAmountShouldThrowWhenFromCardNotFound() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);

        when(cardService.getById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transferAmount(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sender card not found");
    }

    @Test
    void transferAmountShouldThrowWhenToCardNotFound() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);

        Card fromCard = mock(Card.class);

        when(cardService.getById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transferService.transferAmount(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Receiver card not found");
    }

    @Test
    void transferAmountShouldThrowWhenFromCardIsInactive() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);

        when(cardService.getById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getById(2L)).thenReturn(Optional.of(toCard));
        when(fromCard.isActive()).thenReturn(false);

        assertThatThrownBy(() -> transferService.transferAmount(request))
                .isInstanceOf(InactiveCardException.class)
                .hasMessageContaining("Card");
    }

    @Test
    void transferAmountShouldThrowWhenToCardIsInactive() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.TEN);

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);

        when(cardService.getById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getById(2L)).thenReturn(Optional.of(toCard));
        when(fromCard.isActive()).thenReturn(true);
        when(toCard.isActive()).thenReturn(false);

        assertThatThrownBy(() -> transferService.transferAmount(request))
                .isInstanceOf(InactiveCardException.class)
                .hasMessageContaining("Card");
    }

    @Test
    void transferAmountshouldDoNothingWhenAmountIsZero() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.ZERO);

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);

        when(cardService.getById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getById(2L)).thenReturn(Optional.of(toCard));
        when(fromCard.isActive()).thenReturn(true);
        when(toCard.isActive()).thenReturn(true);

        transferService.transferAmount(request);

        verify(fromCard).withdraw(BigDecimal.ZERO);
        verify(toCard).deposit(BigDecimal.ZERO);
    }

    @Test
    void transferAmountShouldThrowWhenAmountIsNegative() {
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("-10"));

        Card fromCard = mock(Card.class);
        Card toCard = mock(Card.class);

        when(cardService.getById(1L)).thenReturn(Optional.of(fromCard));
        when(cardService.getById(2L)).thenReturn(Optional.of(toCard));
        when(fromCard.isActive()).thenReturn(true);
        when(toCard.isActive()).thenReturn(true);

        transferService.transferAmount(request);

        verify(fromCard).withdraw(request.getAmount());
        verify(toCard).deposit(request.getAmount());
    }

}

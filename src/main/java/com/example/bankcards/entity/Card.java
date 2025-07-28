package com.example.bankcards.entity;

import com.example.bankcards.util.EncryptionUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String encryptedNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;

    private LocalDate expiredDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal balance;

    public boolean isActive() {
        return status == Status.ACTIVE;
    }

    public boolean hasSufficientFunds(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public void withdraw(BigDecimal amount) {
        if (!hasSufficientFunds(amount)) {
            throw new IllegalStateException("Insufficient funds");
        }
        balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void setEncryptedNumber(String number) {
        this.encryptedNumber = EncryptionUtil.encrypt(number);
    }

    @Transient
    public String getEncryptedNumber() {
        String number = EncryptionUtil.decrypt(encryptedNumber);
        return "**** **** **** " + number.substring(number.length() - 4);
    }
}

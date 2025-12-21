package com.mikusmoney.mikusMoney.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "sends")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Send extends Transaction {

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "sender_miku_id", nullable = false)
    private Miku sender;

    @ManyToOne
    @JoinColumn(name = "receiver_miku_id", nullable = false)
    private Miku receiver;

    @PrePersist
    protected void onCreate() {
        setTransactionType("SEND");
        validateTransaction();
    }

    private void validateTransaction() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Send amount must be greater than zero");
        }
        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("Sender and receiver must not be null");
        }
        if (sender.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("Cannot send money to yourself");
        }
    }

    public boolean isLargeTransfer() {
        return amount.compareTo(new BigDecimal("10000")) >= 0;
    }

    public boolean isSelfTransfer() {
        return sender != null && receiver != null && sender.getId().equals(receiver.getId());
    }
}

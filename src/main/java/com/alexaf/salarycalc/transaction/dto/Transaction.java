package com.alexaf.salarycalc.transaction.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.alexaf.salarycalc.transaction.repository.TransactionEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction implements Serializable {

    private UUID id;

    private LocalDateTime created;

    @NotNull
    private Long initiatorId;

    @NotNull
    private UUID accrualAccountId;

    @NotNull
    private BigDecimal amount;

    private LocalDateTime completedAt;

    public Transaction(Long initiatorId, UUID accrualAccountId, BigDecimal amount) {
        this.initiatorId = initiatorId;
        this.accrualAccountId = accrualAccountId;
        this.amount = amount;
    }

}
package com.alexaf.salarycalc.account.dto;

import com.alexaf.salarycalc.account.repository.AccountEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link AccountEntity}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private UUID id;

    private LocalDateTime created;

    private BigDecimal balance;

    @NotNull
    private Long ownerId;

}

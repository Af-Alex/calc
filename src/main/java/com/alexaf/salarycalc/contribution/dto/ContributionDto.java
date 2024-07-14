package com.alexaf.salarycalc.contribution.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.alexaf.salarycalc.contribution.repository.Contribution}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContributionDto implements Serializable {

    private UUID id;

    private LocalDateTime created;

    @NotNull
    private UUID goalId;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime contributionDateTime;

    public ContributionDto(UUID goalId, BigDecimal contributionValue, LocalDateTime now) {
        this.goalId = goalId;
        this.amount = contributionValue;
        this.contributionDateTime = now;
    }
}
package com.alexaf.salarycalc.goal.dto;

import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.goal.repository.GoalSaveRequest;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link GoalSaveRequest}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalSaveRequestDto implements Serializable {

    private UUID id;

    private LocalDateTime created;

    @NotNull
    private UUID userId;

    @Length(min = 1, max = 50)
    private String name;

    private Integer priority;

    private GoalType type;

    private BigDecimal balance = BigDecimal.ZERO;

    private BigDecimal targetAmount;

    @Future
    private LocalDate deadline;

    private BigDecimal monthlyAmount;

    public GoalSaveRequestDto(UUID userId) {
        this.userId = userId;
    }
}
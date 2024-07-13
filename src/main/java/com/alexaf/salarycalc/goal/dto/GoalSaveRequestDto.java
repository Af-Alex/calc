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

import static java.util.Objects.nonNull;

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

    @Override
    public String toString() {
        var sb = new StringBuilder();
        if (nonNull(name)) sb.append("\nНазвание: ").append(name);
        if (nonNull(priority)) sb.append("\nПриоритет: ").append(priority);
        if (nonNull(type)) sb.append("\nТип: ").append(type.getText());
        if (nonNull(monthlyAmount)) {
            sb.append("\nЕжемесячная сумма: ").append(monthlyAmount);
            if (type.name().contains("PERCENT")) sb.append("%");
        }
        if (nonNull(targetAmount)) sb.append("\nОткладывать до накопления суммы: ").append(targetAmount);
        if (nonNull(deadline)) sb.append("\nОткладывать до: ").append(deadline);
        return sb.toString();
    }
}
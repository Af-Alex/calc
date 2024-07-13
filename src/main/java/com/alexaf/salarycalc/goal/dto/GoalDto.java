package com.alexaf.salarycalc.goal.dto;

import com.alexaf.salarycalc.goal.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.alexaf.salarycalc.goal.repository.Goal}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalDto implements Serializable {
    private UUID id;

    private LocalDateTime created;

    private UUID userId;

    @Size(message = "Имя цели должно иметь от 1 до 50 символов", min = 1, max = 50)
    @NotBlank(message = "Цель не может быть без имени")
    private String name;

    @NotNull(message = "Цель не может быть без приоритета")
    private Integer priority = 0;

    @NotNull(message = "У цели отсутствует тип")
    private GoalType type;

    private BigDecimal balance = BigDecimal.ZERO;

    private BigDecimal targetAmount;

    private LocalDate deadline;

    private boolean active = true;

    @NotNull(message = "Значение ужумесячной суммы не может отсутствовать")
    @Positive(message = "Нельзя откладывать себе в убыток")
    private BigDecimal monthlyAmount;
}
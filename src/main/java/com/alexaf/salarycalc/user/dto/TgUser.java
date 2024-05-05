package com.alexaf.salarycalc.user.dto;

import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.user.repository.TgUserEntity;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for {@link TgUserEntity}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TgUser implements Serializable {

    @NotNull
    private Long id;

    private LocalDateTime created;

    @Size(message = "Имя пользователя должно иметь длину от 1 до 100 символов", min = 1, max = 100)
    @NotBlank
    private String userName;

    @NotNull
    private Boolean active;

    @Digits(message = "Значение дохода сокращается до 2 знаков после запятой", integer = 10, fraction = 2)
    @Positive(message = "Доход должен быть положительным значением")
    private BigDecimal salary;

    @NotNull
    private ChatState chatState;

    private String firstName;

    private String lastName;
}
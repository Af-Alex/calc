package com.alexaf.salarycalc.auth.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на регистрацию")
public class SignUpRequest {

    @Schema(description = "Имя пользователя", example = "Alex")
    @Size(min = 4, max = 100, message = "Имя пользователя должно содержать от 4 до 100 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Пароль", example = "super_secret")
    @Size(min = 8, max = 72, message = "Длина пароля должна быть от 8 до 72 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
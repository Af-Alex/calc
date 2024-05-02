package com.alexaf.salarycalc.auth.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на аутентификацию")
public class SignInRequest {

    @Schema(description = "Имя пользователя", example = "admin")
    @Size(min = 4, max = 100, message = "Имя пользователя должно содержать от 4 до 100 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Пароль", example = "admin")
    @Size(min = 4, max = 72, message = "Длина пароля должна быть от 4 до 72 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}
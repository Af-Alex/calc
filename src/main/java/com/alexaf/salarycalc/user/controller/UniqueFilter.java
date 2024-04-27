package com.alexaf.salarycalc.user.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Получение данных пользователя по уникальному полю")
public record UniqueFilter(

        @Parameter(description = "Имя пользователя", example = "Alex")
        @Size(min = 4, max = 100, message = "Имя пользователя должно содержать от 4 до 100 символов")
        @NotBlank(message = "Имя пользователя не может быть пустыми")
        String username

) {}

package com.alexaf.salarycalc.user.controller;

import com.alexaf.salarycalc.user.dto.Role;
import com.alexaf.salarycalc.user.dto.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Данные пользователя")
public record UserResponse(
        @NotNull
        UUID id,
        @NotBlank
        String username,
        @NotNull
        Role role,
        @NotNull
        Boolean enabled,
        @NotNull
        LocalDateTime created

) implements Serializable {
    public UserResponse(User user) {
        this(user.getId(), user.getUsername(), user.getRole(), user.isEnabled(), user.getCreated());
    }
}
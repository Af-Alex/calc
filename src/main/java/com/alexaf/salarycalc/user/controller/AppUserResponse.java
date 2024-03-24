package com.alexaf.salarycalc.user.controller;

import com.alexaf.salarycalc.user.dto.AppUser;
import com.alexaf.salarycalc.user.dto.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "User", description = "Response for getting user data")
public record AppUserResponse(
        @NotNull
        UUID id,
        @NotBlank
        String username,
        @NotNull
        UserRole role,
        @NotNull
        Boolean enabled,
        @NotNull
        LocalDateTime created
) implements Serializable {
    public AppUserResponse(AppUser appUser) {
        this(appUser.getId(), appUser.getUsername(), appUser.getRole(), appUser.isEnabled(), appUser.getCreated());
    }
}
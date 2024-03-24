package com.alexaf.salarycalc.user.registration;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

public record RegistrationData(
        @NotBlank @Size(min = 4,max = 100)
        String username,
        @NotBlank @Size(min = 8, max = 72)
        String password
) implements Serializable {
}
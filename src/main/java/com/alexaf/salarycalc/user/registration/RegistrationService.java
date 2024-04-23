package com.alexaf.salarycalc.user.registration;

import com.alexaf.salarycalc.user.AppUserService;
import com.alexaf.salarycalc.user.dto.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final PasswordEncoder passwordEncoder;

    public UUID registerUser(RegistrationData registrationData) {
        return appUserService.createUser(
                registrationData.username(),
                passwordEncoder.encode(registrationData.password()),
                UserRole.USER
        );
    }
}

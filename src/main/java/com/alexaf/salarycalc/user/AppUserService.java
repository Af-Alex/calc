package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.user.dto.AppUser;
import com.alexaf.salarycalc.user.dto.UserRole;
import com.alexaf.salarycalc.user.entity.AppUserEntity;
import com.alexaf.salarycalc.user.repository.AppUserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return appUserRepository.findByUsername(username)
                .map(appUserMapper::toDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public UUID createUser(String username, String password, UserRole role) {
        AppUserEntity user = new AppUserEntity();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setEnabled(true);
        return appUserRepository.save(user).getId();
    }

    public void deleteUser(UUID id) {
        appUserRepository.deleteById(id);
    }

    public AppUser getUser(UUID userId) {
        return appUserRepository.findById(userId).map(appUserMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(format("User [%s] not found", userId)));
    }

}

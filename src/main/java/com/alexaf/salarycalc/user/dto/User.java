package com.alexaf.salarycalc.user.dto;

import com.alexaf.salarycalc.user.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

/**
 * DTO for {@link UserEntity}
 */
@Data
@Builder
public class User implements UserDetails {

    private UUID id;

    @Size(min = 4, max = 100, message = "Имя пользователя должно содержать от 4 до 100 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    private String password;

    private Role role;

    private boolean enabled;

    private LocalDateTime created;

    private Long telegramId;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return isEnabled();
    }

    @Override
    public boolean isAccountNonLocked() {
        return isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isEnabled();
    }

}

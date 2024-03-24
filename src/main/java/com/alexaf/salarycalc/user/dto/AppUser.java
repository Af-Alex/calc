package com.alexaf.salarycalc.user.dto;

import com.alexaf.salarycalc.user.entity.AppUserEntity;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * DTO for {@link AppUserEntity}
 */
@Data
public class AppUser implements UserDetails {

    private UUID id;
    private String username;
    private String password;
    private UserRole role;
    private boolean enabled;
    private LocalDateTime created;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
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

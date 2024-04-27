package com.alexaf.salarycalc.user.dto;

import com.alexaf.salarycalc.user.entity.UserEntity;
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
    private String username;
    private String password;
    private Role role;
    private boolean enabled;
    private LocalDateTime created;


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

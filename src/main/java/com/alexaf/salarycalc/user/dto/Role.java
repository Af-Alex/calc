package com.alexaf.salarycalc.user.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.alexaf.salarycalc.user.dto.Authority.ADMIN_CREATE;
import static com.alexaf.salarycalc.user.dto.Authority.ADMIN_DELETE;
import static com.alexaf.salarycalc.user.dto.Authority.ADMIN_READ;
import static com.alexaf.salarycalc.user.dto.Authority.ADMIN_UPDATE;
import static com.alexaf.salarycalc.user.dto.Authority.MANAGER_CREATE;
import static com.alexaf.salarycalc.user.dto.Authority.MANAGER_DELETE;
import static com.alexaf.salarycalc.user.dto.Authority.MANAGER_READ;
import static com.alexaf.salarycalc.user.dto.Authority.MANAGER_UPDATE;
import static java.util.Collections.emptySet;

@Getter
@RequiredArgsConstructor
public enum Role {

    USER(emptySet()),
    ADMIN(
            Set.of(
                    ADMIN_READ,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    ADMIN_CREATE,
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    ),
    MANAGER(
            Set.of(
                    MANAGER_READ,
                    MANAGER_UPDATE,
                    MANAGER_DELETE,
                    MANAGER_CREATE
            )
    );

    private final Set<Authority> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getAuthority()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

}

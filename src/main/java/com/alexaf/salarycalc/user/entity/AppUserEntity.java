package com.alexaf.salarycalc.user.entity;

import com.alexaf.salarycalc.user.dto.UserRole;
import com.alexaf.salarycalc.utils.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "app_user", uniqueConstraints = {
        @UniqueConstraint(name = "uc_app_user_username", columnNames = {"username"})
})
@Getter
@Setter
public class AppUserEntity extends BaseEntity {

    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 72, nullable = false)
    private String password;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false, length = 10)
    private UserRole role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

}

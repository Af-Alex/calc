package com.alexaf.salarycalc.user.entity;

import com.alexaf.salarycalc.user.dto.Role;
import com.alexaf.salarycalc.utils.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;

@Entity
@Table(name = "app_user", uniqueConstraints = {
        @UniqueConstraint(name = "uc_app_user_username", columnNames = {"username"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 72, nullable = false)
    private String password;

    @Enumerated(STRING)
    @Column(name = "role", nullable = false, length = 10)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

}

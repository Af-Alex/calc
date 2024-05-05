package com.alexaf.salarycalc.user.repository;

import com.alexaf.salarycalc.telegram.ChatState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "tg_user")
@Getter
@Setter
@NoArgsConstructor
public class TgUserEntity {

    @Id
    @NotNull
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "created", nullable = false, updatable = false)
    private LocalDateTime created;

    @NotBlank
    @Size(max = 100, min = 1, message = "Имя пользователя должно иметь длину от 1 до 100 символов")
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String userName;

    @NotNull
    @Column(name = "active", nullable = false)
    private Boolean active;

    @Digits(integer = 10, fraction = 2, message = "Значение дохода сокращается до 2 знаков после запятой")
    @Positive(message = "Доход должен быть положительным значением")
    @Column(name = "salary", precision = 10, scale = 2)
    private BigDecimal salary;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "chat_state", nullable = false, length = 50)
    private ChatState chatState;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        TgUserEntity that = (TgUserEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

}
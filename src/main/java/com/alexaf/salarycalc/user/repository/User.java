package com.alexaf.salarycalc.user.repository;

import com.alexaf.salarycalc.goal.repository.Goal;
import com.alexaf.salarycalc.salary.repository.Salary;
import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.utils.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User extends BaseEntity {

    @UpdateTimestamp
    @Column(name = "last_login")
    private LocalDateTime lastLogin; // обновляется с chatState, для контроллера дописать логику

    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "telegram_nickname", length = 100)
    private String telegramNickname;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "firstname", length = 100)
    private String firstname;

    @Column(name = "lastname", length = 100)
    private String lastname;

    @Column(name = "chat_state", length = 50)
    @Enumerated(EnumType.STRING)
    private ChatState chatState;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Goal> goals;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Salary> salaries;

    /**
     * User from telegram
     */
    public User(Long telegramId, String telegramNickname, String firstname, String lastname, ChatState chatState) {
        this.telegramId = telegramId;
        this.telegramNickname = telegramNickname;
        this.firstname = firstname;
        this.lastname = lastname;
        this.chatState = chatState;
    }

    /**
     * User from API
     */
    public User(String email, String password, String firstname, String lastname) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
    }

}
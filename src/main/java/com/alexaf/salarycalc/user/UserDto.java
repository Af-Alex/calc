package com.alexaf.salarycalc.user;

import com.alexaf.salarycalc.telegram.statics.ChatState;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for {@link com.alexaf.salarycalc.user.repository.User}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto implements Serializable {
    private UUID id;
    private LocalDateTime created;
    private LocalDateTime lastLogin;
    private Long telegramId;
    private String telegramNickname;
    private String email;
    @ToString.Exclude //todo: добавить TelegramUserSaveRequest
    private String password;
    private boolean active;
    private String firstname;
    private String lastname;
    private ChatState chatState;
}
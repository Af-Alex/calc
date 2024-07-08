package com.alexaf.salarycalc.telegram.service;

import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.telegram.TelegramConfig;
import com.alexaf.salarycalc.user.UserDto;
import com.alexaf.salarycalc.user.UserMapper;
import com.alexaf.salarycalc.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@ConditionalOnBean(TelegramConfig.class)
public class TelegramService {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserDto registerUser(@Valid @NotNull User user) {
        var userDto = UserDto.builder()
                .telegramId(user.getId())
                .active(true)
                .telegramNickname(user.getUserName())
                .firstname(user.getFirstName())
                .lastname(user.getLastName())
                .chatState(ChatState.WELCOME)
                .build();

        return userService.create(userDto);
    }

    public Optional<UserDto> find(User user) {
        return userService.findByTelegramId(user.getId());
    }

    public boolean existsById(Long userId) {
        return userService.existsByTelegramId(userId);
    }

    public ChatState getUserChatState(Long userId) {
        UserDto user = userService.getByTelegramId(userId);
        return user.getChatState();
    }

    public void updateChatState(Long userId, ChatState newState) {
        userService.updateChatState(userId, newState);
    }

}

package com.alexaf.salarycalc.telegram.service;

import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.user.TgUserMapper;
import com.alexaf.salarycalc.user.TgUserService;
import com.alexaf.salarycalc.user.dto.TgUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Optional;

@Slf4j
@Service
@Transactional
public class TelegramService {

    private final TgUserService userService;
    private final TgUserMapper userMapper;

    public TelegramService(TgUserService userService, TgUserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    public TgUser registerUser(@Valid @NotNull User user) {
        var dto = userMapper.toDto(user);
        dto.setActive(true);
        dto.setChatState(ChatState.WELCOME);
        return userService.save(dto);
    }

    public Optional<TgUser> find(User user) {
        return userService.find(user.getId());

    }

    public boolean existsById(Long userId) {
        return userService.existsById(userId);
    }

    public ChatState getUserChatState(Long userId) {
        TgUser user = userService.get(userId);
        return user.getChatState();
    }

    public void updateChatState(Long userId, ChatState newState) {
        TgUser user = userService.get(userId);
        user.setChatState(newState);
        userService.save(user);
    }

}

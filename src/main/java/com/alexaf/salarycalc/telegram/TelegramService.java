package com.alexaf.salarycalc.telegram;

import com.alexaf.salarycalc.user.UserService;
import com.alexaf.salarycalc.user.dto.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TelegramService {

    private final UserService userService;


    public void checkUser(long chatId) {
        if (userService.existsByTelegramId(chatId))
            return;
        UUID userId = userService.create("admin", "admin", Role.ADMIN, chatId);
    }

}

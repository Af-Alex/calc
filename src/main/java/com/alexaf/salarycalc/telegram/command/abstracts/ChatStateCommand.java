package com.alexaf.salarycalc.telegram.command.abstracts;

import com.alexaf.salarycalc.telegram.command.Command;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public abstract class ChatStateCommand implements Command {

    @Getter
    private final ChatState state;
    protected UserService userService;

    public ChatStateCommand(ChatState state) {
        this.state = state;
    }

    protected void updateUserChatState(UUID userId, ChatState newState) {
        userService.updateChatState(userId, newState);
    }

    @Autowired
    private void setUserService(UserService userService) {
        this.userService = userService;
    }

}

package com.alexaf.salarycalc.telegram.command.menu;

import com.alexaf.salarycalc.telegram.command.abstracts.SendCommand;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class MainMenuCommand extends SendCommand {
    public MainMenuCommand() {
        super(ChatState.MAIN_MENU);
    }

    protected MainMenuCommand(ChatState state) {
        super(state);
    }

    @Override
    public void execute(Update update, UserDto user) {
        sendCurrentState(user, false);
        sender.send("Выбери действие", user);

    }

    protected void forceUserToMainMenu(Update update, UserDto user) {
        updateUserChatState(user.getId(), ChatState.MAIN_MENU);
        this.execute(update, user);
    }

    protected void unkwonButtonReply(Update update, UserDto user) {
        updateUserChatState(user.getId(), ChatState.MAIN_MENU);
        this.execute(update, user);
    }

}

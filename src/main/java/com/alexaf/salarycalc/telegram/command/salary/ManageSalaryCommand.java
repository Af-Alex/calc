package com.alexaf.salarycalc.telegram.command.salary;

import com.alexaf.salarycalc.telegram.command.menu.MainMenuCommand;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.telegram.statics.KeyboardFactory;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class ManageSalaryCommand extends MainMenuCommand {

    public ManageSalaryCommand() {
        super(ChatState.MANAGE_SALARY);
    }

    protected ManageSalaryCommand(ChatState state) {
        super(state);
    }

    @Override
    public void execute(Update update, UserDto user) {
        sendCurrentState(user, true);
        reply(user.getTelegramId(), "Выбери действие", KeyboardFactory.getKeyboard(getState(), true));


    }

}

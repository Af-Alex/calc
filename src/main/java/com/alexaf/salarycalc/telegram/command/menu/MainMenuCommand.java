package com.alexaf.salarycalc.telegram.command.menu;

import com.alexaf.salarycalc.telegram.command.abstracts.SendCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static com.alexaf.salarycalc.telegram.statics.ChatState.MAIN_MENU;
import static com.alexaf.salarycalc.telegram.statics.ChatState.MANAGE_CONTRIBUTION;
import static com.alexaf.salarycalc.telegram.statics.ChatState.MANAGE_GOAL;
import static com.alexaf.salarycalc.telegram.statics.ChatState.MANAGE_SALARY;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.mainMenuKeyboard;
import static java.util.Optional.ofNullable;

@Component
public class MainMenuCommand extends SendCommand {
    public MainMenuCommand() {
        super(MAIN_MENU);
    }

    protected MainMenuCommand(ChatState state) {
        super(state);
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            switch (button) {
                case GOALS -> reply(user.getTelegramId(), "Управление целями", getKeyboard(MANAGE_GOAL, true), MANAGE_GOAL);
                case CONTRIBUTION -> reply(user.getTelegramId(), "Управление поступлениями", getKeyboard(MANAGE_CONTRIBUTION, true), MANAGE_CONTRIBUTION);
                case SALARY -> reply(user.getTelegramId(), "Управление зарплатой", getKeyboard(MANAGE_SALARY, true), MANAGE_SALARY);
                default -> chooseButtonReply(user);
            }
        } catch (IllegalArgumentException ignore) {
            reply(user.getTelegramId(), "Выбери действие из кнопок на панели", getKeyboard(getState(), true));
        }
    }

    protected void forceUserToMainMenu(UserDto user) {
        reply(user.getTelegramId(), "Выбери действие", mainMenuKeyboard(), MAIN_MENU);
    }

    protected void unknownMessageReply(UserDto user) {
        sender.send("Не удалось прочитать значение. Возвращаемся в главное меню", user);
        forceUserToMainMenu(user);
    }

    protected void chooseButtonReply(UserDto user) {
        reply(user.getTelegramId(), "Не удалось прочитать значение. Выбери действие с помощью кнопок", getKeyboard(getState()));
    }

    protected void processMainMenuButton(Update update, UserDto user) {
        ofNullable(update.getMessage()).map(Message::getText).ifPresent(text -> {
            try {
                if (Button.getByText(text) == Button.MAIN_MENU)
                    forceUserToMainMenu(user);
            } catch (IllegalArgumentException notButton) {
                chooseButtonReply(user);
            }
        });
    }

}

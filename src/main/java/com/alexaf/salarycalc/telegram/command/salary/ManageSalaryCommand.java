package com.alexaf.salarycalc.telegram.command.salary;

import com.alexaf.salarycalc.exception.MethodNotImplementedException;
import com.alexaf.salarycalc.telegram.command.menu.MainMenuCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;

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
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case SALARY -> reply(user.getTelegramId(), "Здесь можно добавить, удалить, прочитать или обновить свою зарплату", getKeyboard(getState(), true));
                case ADD -> {
                    reply(user.getTelegramId(), "Укажи значение зарплаты", getKeyboard(ChatState.ADD_SALARY, true));
                    updateUserChatState(user.getId(), ChatState.ADD_SALARY);
                }
                case DELETE -> throw new MethodNotImplementedException("Удаление зарплаты");
                case UPDATE -> throw new MethodNotImplementedException("Обновление зарплаты");
                case GET -> throw new MethodNotImplementedException("Получение информации о зарплате");
                default -> unknownMessageReply(user);
            }
        } catch (IllegalArgumentException ignore) {
            reply(user.getTelegramId(), "Выбери действие из кнопок на панели", getKeyboard(getState(), true));
        }
    }

}

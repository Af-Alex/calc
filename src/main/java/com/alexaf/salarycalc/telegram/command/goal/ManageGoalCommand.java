package com.alexaf.salarycalc.telegram.command.goal;

import com.alexaf.salarycalc.exception.MethodNotImplementedException;
import com.alexaf.salarycalc.goal.GoalService;
import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.telegram.command.menu.MainMenuCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;

@Component
public class ManageGoalCommand extends MainMenuCommand {

    protected GoalService goalService;

    public ManageGoalCommand() {
        super(ChatState.MANAGE_GOAL);
    }

    protected ManageGoalCommand(ChatState state) {
        super(state);
    }

    @Autowired
    private void setGoalService(GoalService goalService) {
        this.goalService = goalService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case ADD -> processAddGoal(user);
                case DELETE -> throw new MethodNotImplementedException("Удаление цели");
                case UPDATE -> throw new MethodNotImplementedException("Обновление цели");
                case GET -> throw new MethodNotImplementedException("Получение информации о цели");
                default -> unknownMessageReply(user);
            }
        } catch (IllegalArgumentException notButton) {
            chooseButtonReply(user);
        }
    }

    private void processAddGoal(UserDto user) {
        StringBuilder sb = new StringBuilder("Цели можно ставить по-разному. Какой тип тебе подходит?\n");

        for (int i = 0; i < GoalType.AVAILABLE_TYPES.length; i++) {
            sb.append("\n").append(i + 1).append(". ").append(GoalType.AVAILABLE_TYPES[i].getText());
        }

        reply(user.getTelegramId(), sb.toString(), getKeyboard(ChatState.ADD_GOAL_TYPE, true));
        updateUserChatState(user.getId(), ChatState.ADD_GOAL_TYPE);
    }

}

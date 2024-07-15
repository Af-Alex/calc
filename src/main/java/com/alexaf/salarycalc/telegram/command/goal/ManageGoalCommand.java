package com.alexaf.salarycalc.telegram.command.goal;

import com.alexaf.salarycalc.exception.MethodNotImplementedException;
import com.alexaf.salarycalc.goal.GoalService;
import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.goal.dto.GoalDto;
import com.alexaf.salarycalc.telegram.command.menu.MainMenuCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getInlineButtonsRows;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.mainMenuKeyboard;

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
                case UPDATE -> processUpdateGoal(user);
                case GET -> processGetGoal(user);
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

    private void processUpdateGoal(UserDto user) {
        var goals = goalService.findActiveByUserIdSortByPriority(user.getId());

        if (goals.isEmpty()) {
            sender.send("У тебя нет целей, которые можно было бы обновить.", user);
            return;
        }

        var inlineKeyboard = new InlineKeyboardMarkup(
                getInlineButtonsRows(goals.stream()
                        .map(goal -> Pair.of(goal.getName(), goal.getId().toString()))
                        .toList())
        );

        reply(user.getTelegramId(), "Какую цель хочешь обновить?", inlineKeyboard, ChatState.UPDATE_GOAL_CHOOSE_FIELD);
    }

    private void processGetGoal(UserDto user) {
        var goals = goalService.findActiveByUserIdSortByPriority(user.getId());

        if (goals.isEmpty()) {
            reply(user.getTelegramId(), "Цели ещё не добавлены. Используй кнопку \"Добавить\"", getKeyboard(getState(), true));
            return;
        }

        var sb = new StringBuilder("Твои цели:\n");

        int counter = 1;
        for (GoalDto goal : goals) {
            sb.append(counter++).append(")\n")
                    .append("\t").append("Название: ").append(goal.getName()).append("\n")
                    .append("\t").append("Тип: ").append(goal.getType().getText()).append("\n")
                    .append("\t").append("Месячная сумма: ").append(goal.getMonthlyAmount()).append("\n")
                    .append("\t").append("Приоритет: ").append(goal.getPriority()).append("\n");
        }

        reply(user.getTelegramId(), sb.toString(), mainMenuKeyboard(), ChatState.MAIN_MENU);
    }

}

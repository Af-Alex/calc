package com.alexaf.salarycalc.telegram.command.goal.update;

import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.UUID;

import static com.alexaf.salarycalc.telegram.statics.ChatState.UPDATE_GOAL_CHOOSE_FIELD;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getInlineButtonsRows;

@Component
public class UpdateGoalChooseFieldCommand extends ManageGoalCommand {

    public UpdateGoalChooseFieldCommand() {
        super(UPDATE_GOAL_CHOOSE_FIELD);
    }

    @Override
    public void execute(Update update, UserDto user) {
        processMainMenuButton(update, user);
        var goalId = UUID.fromString(update.getCallbackQuery().getData());

        var inlineKeyboard = new InlineKeyboardMarkup(
                getInlineButtonsRows(List.of(
                        Pair.of("1", goalId + ":name"),
                        Pair.of("2", goalId + ":monthlyAmount")
                ))
        );

        reply(
                user.getTelegramId(),
                "Какой параметр меняем?\n1. Наименование цели\n2. Месячное значение цели",
                inlineKeyboard,
                ChatState.UPDATE_GOAL
        );
    }

}

package com.alexaf.salarycalc.telegram.command.goal.add;

import com.alexaf.salarycalc.goal.GoalSaveRequestService;
import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.KeyboardFactory;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_PRIORITY;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_SAVE_CHECK;
import static com.alexaf.salarycalc.telegram.statics.ChatState.MANAGE_GOAL;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;
import static java.lang.String.format;

@Component
public class AddGoalSaveCheckCommand extends ManageGoalCommand {

    private final GoalSaveRequestService goalSaveRequestService;

    public AddGoalSaveCheckCommand(GoalSaveRequestService goalSaveRequestService) {
        super(ADD_GOAL_SAVE_CHECK);
        this.goalSaveRequestService = goalSaveRequestService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();
        var goalSaveRequest = goalSaveRequestService.getByUserId(user.getId());

        try {
            var button = Button.getByText(message);

            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case REJECT -> {
                    goalSaveRequestService.deleteById(goalSaveRequest.getId());
                    reply(user.getTelegramId(), "Запрос на создание цели удалён. Возвращаемся в главное меню", KeyboardFactory.mainMenuKeyboard());
                }
                case ACCEPT -> {
                    goalSaveRequestService.finish(goalSaveRequest);
                    reply(
                            user.getTelegramId(),
                            "Цель сохранена, возвращаемся в меню управления целями",
                            getKeyboard(MANAGE_GOAL, true),
                            MANAGE_GOAL
                    );
                }
                default -> chooseButtonReply(user);
            }

        } catch (IllegalArgumentException ignore) {
            chooseButtonReply(user);
        }
    }

    private void processPriorityValue(UserDto user, GoalSaveRequestDto goalSaveRequest, String message) {
        try {
            var priority = Integer.valueOf(message);
            goalSaveRequest.setPriority(priority);
            goalSaveRequestService.update(goalSaveRequest);

            sender.send("Установлен приоритет: " + priority, user);
            forceUserToGoalSaveCheck(user);
        } catch (NumberFormatException e) {
            reply(
                    user.getTelegramId(),
                    format("Не удалось прочитать указанное значение. Укажи целочисленное значение между %s и %s", Integer.MIN_VALUE, Integer.MAX_VALUE),
                    getKeyboard(getState())
            );
        }
    }

    private void forceUserToGoalSaveCheck(UserDto user) {

        reply(
                user.getTelegramId(),
                "Проверь введённые данные:\n",
                getKeyboard(ADD_GOAL_PRIORITY, true)
        );
    }

}

package com.alexaf.salarycalc.telegram.command.goal.add;

import com.alexaf.salarycalc.goal.GoalSaveRequestService;
import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

import static com.alexaf.salarycalc.telegram.statics.Button.NUMBERS;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_PRIORITY;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_SAVE_CHECK;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;
import static java.lang.String.format;

@Component
public class AddGoalPriorityCommand extends ManageGoalCommand {

    private final GoalSaveRequestService goalSaveRequestService;

    public AddGoalPriorityCommand(GoalSaveRequestService goalSaveRequestService) {
        super(ADD_GOAL_PRIORITY);
        this.goalSaveRequestService = goalSaveRequestService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();
        var goalSaveRequest = goalSaveRequestService.getByUserId(user.getId());

        try {
            var button = Button.getByText(message);

            if (Arrays.stream(NUMBERS).anyMatch(numberButton -> numberButton == button)) {
                processPriorityValue(user, goalSaveRequest, message);
                return;
            }

            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case HELP -> sender.send("Приоритет - это целочисленное значение, обозначающее, в какой последовательности будет рассчитываться сумма от дохода, полагающаяся для этой цели. Стандартное значение приоритета 0. Если у тебя есть 2 цели с приоритетами 1 и 2, то сначала рассчитается 1, а только затем - 2. Это может пригодиться, если ты хочешь создать отдельную цель, которая вычисляется исходя из остатка или наоборот, обязательно перед всеми", user);
                default -> sender.send("Ты попал в кнопку, но такого обработчика нет. Напиши другое значение", user);
            }

        } catch (IllegalArgumentException ignore) {
            processPriorityValue(user, goalSaveRequest, message);
        }
    }

    private void processPriorityValue(UserDto user, GoalSaveRequestDto goalSaveRequest, String message) {
        try {
            var priority = Integer.valueOf(message);
            goalSaveRequest.setPriority(priority);
            goalSaveRequestService.update(goalSaveRequest);

            sender.send("Установлен приоритет: " + priority, user);
            forceUserToGoalSaveCheck(user, goalSaveRequest);
        } catch (NumberFormatException e) {
            reply(
                    user.getTelegramId(),
                    format("Не удалось прочитать указанное значение. Укажи целочисленное значение между %s и %s", Integer.MIN_VALUE, Integer.MAX_VALUE),
                    getKeyboard(getState())
            );
        }
    }

    private void forceUserToGoalSaveCheck(UserDto user, GoalSaveRequestDto goalSaveRequest) {
        String sb = "Проверь введённые данные:\n" +
                goalSaveRequest +
                "\nВсё верно?";

        reply(
                user.getTelegramId(),
                sb,
                getKeyboard(ADD_GOAL_SAVE_CHECK),
                ADD_GOAL_SAVE_CHECK
        );
    }

}

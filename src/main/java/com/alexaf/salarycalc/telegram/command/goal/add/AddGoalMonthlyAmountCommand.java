package com.alexaf.salarycalc.telegram.command.goal.add;

import com.alexaf.salarycalc.goal.GoalSaveRequestService;
import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;

import static com.alexaf.salarycalc.telegram.statics.Button.NUMBERS;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_MONTHLY_AMOUNT;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_PRIORITY;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;

@Component
public class AddGoalMonthlyAmountCommand extends ManageGoalCommand {

    private final GoalSaveRequestService goalSaveRequestService;

    public AddGoalMonthlyAmountCommand(GoalSaveRequestService goalSaveRequestService) {
        super(ADD_GOAL_MONTHLY_AMOUNT);
        this.goalSaveRequestService = goalSaveRequestService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();
        var goalSaveRequest = goalSaveRequestService.getByUserId(user.getId());

        try {
            var button = Button.getByText(message);
            if (Arrays.stream(NUMBERS).anyMatch(numberButton -> numberButton == button)) {
                processAddMonthlyAmountValue(user, goalSaveRequest, message);
                return;
            }

            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case HELP -> sender.send("Укажи значение, которое хочешь откладывать. К примеру, твоя заработная плата составляет 100 тысяч рублей. Если хочешь откладывать каждый месяц 15% от зарплаты, напиши 15%. Если хочешь откладывать по 30 тысяч - напиши 30000", user);
                default -> sender.send("Ты попал в одну из кнопок. Напиши другое значение", user);
            }

        } catch (IllegalArgumentException ignore) {
            processAddMonthlyAmountValue(user, goalSaveRequest, message);
        }
    }

    private void processAddMonthlyAmountValue(UserDto user, GoalSaveRequestDto goalSaveRequest, String message) {
        try {
            boolean isAmountPercent = message.endsWith("%");
            boolean isGoalTypePercent = goalSaveRequest.getType().name().contains("PERCENT");

            if (!isAmountPercent && isGoalTypePercent) {
                sender.send("Ты указал точное значение для процентной цели, так нельзя. Напиши со знаком процента, сколько хочешь откладывать. Пример: 5%", user);
                return;
            } else if (isAmountPercent && !isGoalTypePercent) {
                sender.send("Ты процентное значение для точной цели, так нельзя. Напиши числовое значение, сколько хочешь откладывать. Пример: 25000", user);
                return;
            }

            var stringAmount = message.replaceAll("[\\s%]", "");
            var amount = new BigDecimal(stringAmount, new MathContext(2, RoundingMode.HALF_DOWN));

            var checkMessage = new StringBuilder("Ты планируешь откладывать: ").append(stringAmount);
            if (isAmountPercent)
                checkMessage.append("%");
            checkMessage.append(" от зарплаты.");
            sender.send(checkMessage.toString(), user);

            goalSaveRequest.setMonthlyAmount(amount);
            goalSaveRequestService.update(goalSaveRequest);
            forceUserToAddGoalPriority(user);
        } catch (NumberFormatException e) {
            sender.send("Не удалось прочитать указанное значение. Если указываешь десятичные значение, пиши их после точки (не запятой)", user);
        }
    }

    private void forceUserToAddGoalPriority(UserDto user) {
        reply(
                user.getTelegramId(),
                "Теперь укажи приоритет цели. Приоритет - это целочисленное значение",
                getKeyboard(ADD_GOAL_PRIORITY),
                ADD_GOAL_PRIORITY
        );
    }

}

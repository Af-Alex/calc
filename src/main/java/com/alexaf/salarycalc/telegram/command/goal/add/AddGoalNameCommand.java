package com.alexaf.salarycalc.telegram.command.goal.add;

import com.alexaf.salarycalc.goal.GoalSaveRequestService;
import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_MONTHLY_AMOUNT;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_NAME;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;
import static java.lang.String.format;

@Slf4j
@Component
public class AddGoalNameCommand extends ManageGoalCommand {

    private final GoalSaveRequestService goalSaveRequestService;

    public AddGoalNameCommand(GoalSaveRequestService goalSaveRequestService) {
        super(ADD_GOAL_NAME);
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
                case HELP -> sender.send("Название цели будет отображаться, когда ты захочешь узнать, как распределить средства. Сделай его коротким и понятным, например, \"На машину\"", user);
                default -> sender.send("К сожалению, это название забронировано системой. Выбери другое", user);
            }

        } catch (IllegalArgumentException ignore) {
            var goalName = message.trim();
            sender.send(format("Так и запишем: \"%s\"", message), user);
            goalSaveRequest.setName(goalName);
            goalSaveRequestService.update(goalSaveRequest);
            forceUserToAddGoalMonthlyAmount(user, goalSaveRequest.getType());
        }
    }

    private void forceUserToAddGoalMonthlyAmount(UserDto user, GoalType chosenType) {
        reply(
                user.getTelegramId(),
                format("Следующий этап - определить, какую сумму от зарплаты или процент от дохода ты хочешь откладывать.\nНапоминаю, твой выбор - \"%s\"", chosenType.getText()),
                getKeyboard(ADD_GOAL_MONTHLY_AMOUNT),
                ADD_GOAL_MONTHLY_AMOUNT
        );
    }

}

package com.alexaf.salarycalc.telegram.command.goal.add;

import com.alexaf.salarycalc.goal.GoalSaveRequestService;
import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.goal.dto.GoalSaveRequestDto;
import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_NAME;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_GOAL_TYPE;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;

@Slf4j
@Component
public class AddGoalTypeCommand extends ManageGoalCommand {

    private final GoalSaveRequestService goalSaveRequestService;

    public AddGoalTypeCommand(GoalSaveRequestService goalSaveRequestService) {
        super(ADD_GOAL_TYPE);
        this.goalSaveRequestService = goalSaveRequestService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            var goalSaveRequest = goalSaveRequestService.findByUserId(user.getId())
                    .orElseGet(() -> goalSaveRequestService.create(new GoalSaveRequestDto(user.getId())));

            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case ONE -> processGoalTypePick(GoalType.AVAILABLE_TYPES[0], goalSaveRequest, user);
                case TWO -> processGoalTypePick(GoalType.AVAILABLE_TYPES[1], goalSaveRequest, user);
                default -> chooseButtonReply(user);
            }
        } catch (IllegalArgumentException ignore) {
            chooseButtonReply(user);
        }

    }

    private void processGoalTypePick(GoalType chosenType, GoalSaveRequestDto saveRequest, UserDto user) {
        sender.send("Выбран тип цели: " + chosenType.getText(), user);
        saveRequest.setType(chosenType);
        goalSaveRequestService.update(saveRequest);
        forceUserToAddGoalName(user);
    }

    private void forceUserToAddGoalName(UserDto user) {
        reply(
                user.getTelegramId(),
                "У хорошей цели - хорошее название! Напиши его в следующем сообщении",
                getKeyboard(ADD_GOAL_NAME),
                ADD_GOAL_NAME
        );
    }

}

package com.alexaf.salarycalc.telegram.command.contribution;

import com.alexaf.salarycalc.contribution.ContributionService;
import com.alexaf.salarycalc.exception.MethodNotImplementedException;
import com.alexaf.salarycalc.telegram.command.menu.MainMenuCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_CONTRIBUTION;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;

@Component
public class ManageContributionCommand extends MainMenuCommand {

    protected ContributionService contributionService;

    public ManageContributionCommand() {
        super(ChatState.MANAGE_CONTRIBUTION);
    }

    protected ManageContributionCommand(ChatState state) {
        super(state);
    }

    @Autowired
    private void setContributionService(ContributionService contributionService) {
        this.contributionService = contributionService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case ADD -> processAddContribution(user);
                case DELETE -> throw new MethodNotImplementedException("Удаление поступлениями");
                case UPDATE -> throw new MethodNotImplementedException("Обновление поступлений");
                case GET -> throw new MethodNotImplementedException("Получение информации о поступлениях");
                default -> unknownMessageReply(user);
            }
        } catch (IllegalArgumentException notButton) {
            chooseButtonReply(user);
        }
    }

    private void processAddContribution(UserDto user) {
        reply(
                user.getTelegramId(),
                "Укажи сумму полученного дохода",
                getKeyboard(ADD_CONTRIBUTION),
                ADD_CONTRIBUTION
        );
    }

}

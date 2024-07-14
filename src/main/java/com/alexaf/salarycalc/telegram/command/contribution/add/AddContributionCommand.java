package com.alexaf.salarycalc.telegram.command.contribution.add;

import com.alexaf.salarycalc.service.DistributionService;
import com.alexaf.salarycalc.telegram.command.contribution.ManageContributionCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

import static com.alexaf.salarycalc.telegram.statics.Button.NUMBERS;
import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_CONTRIBUTION;
import static com.alexaf.salarycalc.telegram.statics.ChatState.MANAGE_CONTRIBUTION;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;

@Component
public class AddContributionCommand extends ManageContributionCommand {

    private final DistributionService distributionService;

    public AddContributionCommand(DistributionService distributionService) {
        super(ADD_CONTRIBUTION);
        this.distributionService = distributionService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            if (Arrays.stream(NUMBERS).anyMatch(numberButton -> numberButton == button)) {
                processAddContribution(user, message);
                return;
            }

            switch (button) {
                case MAIN_MENU -> forceUserToMainMenu(user);
                case HELP -> sender.send("Укажи значение полученного дохода, слитно, разделяя дробную часть точкой. Например, 85360.18", user);
                default -> sender.send("Ты попал в одну из кнопок. Напиши другое значение", user);
            }

        } catch (IllegalArgumentException notButton) {
            processAddContribution(user, message);
        }
    }

    private void processAddContribution(UserDto user, String message) {
        try {
            Map<String, BigDecimal> contributionMap = distributionService.distributeIncome(user.getId(), new BigDecimal(message));
            var sb = new StringBuilder("Распределение дохода по целям:\n");
            contributionMap.forEach((k, v) -> sb.append(k).append(": ").append(v).append("\n"));
            sender.send(sb.toString(), user);
            reply(user.getTelegramId(), "Данные сохранены, возвращаемся в меню управления поступлениями", getKeyboard(MANAGE_CONTRIBUTION, true));
            userService.updateChatState(user.getId(), MANAGE_CONTRIBUTION);
        } catch (NumberFormatException e) {
            sender.send("не удалось прочитать значение. Укажи значение полученного дохода, слитно, разделяя дробную часть точкой. Например, 85360.18", user);
        }
    }


}

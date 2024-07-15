package com.alexaf.salarycalc.telegram.command.goal.update;

import com.alexaf.salarycalc.exception.MethodNotImplementedException;
import com.alexaf.salarycalc.goal.GoalType;
import com.alexaf.salarycalc.telegram.command.goal.ManageGoalCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.UUID;

import static com.alexaf.salarycalc.telegram.statics.ChatState.MANAGE_GOAL;
import static com.alexaf.salarycalc.telegram.statics.ChatState.UPDATE_GOAL;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.getKeyboard;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;

@Component
public class UpdateGoalCommand extends ManageGoalCommand {

    // userId : callbackData
    private final Cache<UUID, String> updateRequestsCache = CacheBuilder.newBuilder()
            .initialCapacity(120)
            .expireAfterWrite(Duration.ofMinutes(2))
            .build();

    public UpdateGoalCommand() {
        super(UPDATE_GOAL);
    }

    @Override
    public void execute(Update update, UserDto user) {

        if (update.getCallbackQuery() != null) { // первый запрос приходит и inline
            updateRequestsCache.put(user.getId(), update.getCallbackQuery().getData());
            sender.send("Введи новое значение", user);
            return;
        }

        var text = update.getMessage().getText();

        try {
            if (Button.getByText(text) == Button.MAIN_MENU)
                forceUserToMainMenu(user);
        } catch (IllegalArgumentException notButton) {
            var callbackData = ofNullable(updateRequestsCache.getIfPresent(user.getId()))
                    .orElseThrow(() -> new IllegalArgumentException("Отсутствуют данные в кэше запроса"));

            var tokens = callbackData.split(":");
            var goal = goalService.getById(UUID.fromString(tokens[0]));
            var field = tokens[1];

            switch (field) {
                case "name" -> {
                    goal.setName(text);
                    goalService.update(goal);
                    reply(user.getTelegramId(), "Наименование цели обновлено", getKeyboard(MANAGE_GOAL, true), MANAGE_GOAL);
                }
                case "monthlyAmount" -> {
                    try {
                        goal.setMonthlyAmount(extractMonthlyAmount(goal.getType(), text));
                        goalService.update(goal);
                        reply(user.getTelegramId(), "Месячное значение цели обновлено", getKeyboard(MANAGE_GOAL, true), MANAGE_GOAL);
                    } catch (IllegalArgumentException e) {
                        sender.send(e.getMessage(), user);
                    } catch (MethodNotImplementedException e) {
                        sender.send(e.getMessage() + ". Возвращаемся в главное меню", user);
                        updateRequestsCache.invalidate(user.getId());
                        forceUserToMainMenu(user);
                    }
                }
            }

            updateRequestsCache.invalidate(user.getId());
        }
    }

    private BigDecimal extractMonthlyAmount(GoalType type, String text) {
        return switch (type) {
            case FIXED_AMOUNT_WITHOUT_DEADLINE -> {
                try {
                    yield new BigDecimal(text);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Для этой цели необходимо указать точное значение. Для разделения десятичных значений используй точку");
                }
            }
            case MONTHLY_PERCENTAGE_WITHOUT_DEADLINE -> {
                try {
                    text = text.replaceAll("[\\s%]", "");
                    var number = new BigDecimal(text, new MathContext(2, RoundingMode.HALF_DOWN));

                    if (number.compareTo(BigDecimal.valueOf(100)) > 0 || number.compareTo(BigDecimal.ZERO) <= 0)
                        throw new IllegalArgumentException();

                    yield number;
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Для этой цели необходимо процентное значение между 0 и 100");
                }
            }
            default -> throw new MethodNotImplementedException(format("Обновление цели типа \"%s\"", type.getText()));
        };
    }

}

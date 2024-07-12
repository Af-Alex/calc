package com.alexaf.salarycalc.telegram.command.salary;

import com.alexaf.salarycalc.salary.SalaryService;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static com.alexaf.salarycalc.telegram.statics.ChatState.ADD_SALARY;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.mainMenuButton;
import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.removeKeyboard;

@Slf4j
@Component
public class AddSalaryCommand extends ManageSalaryCommand {

    private final SalaryService salaryService;

    public AddSalaryCommand(SalaryService salaryService) {
        super(ADD_SALARY);
        this.salaryService = salaryService;
    }

    @Override
    public void execute(Update update, UserDto user) {
        final String message = update.getMessage().getText();

        try {
            var button = Button.getByText(message);
            switch (button) {
                case ADD -> reply(user.getTelegramId(), "Укажи значение зарплаты", mainMenuButton());
                case MAIN_MENU -> forceUserToMainMenu(user);
                default -> {
                    sender.send("Не удалось прочитать значение. Возвращаемся в главное меню", user);
                    forceUserToMainMenu(user);
                }
            }
        } catch (IllegalArgumentException ignore) {
            try {
                var salary = new BigDecimal(message, new MathContext(2, RoundingMode.HALF_UP));
                salaryService.create(user.getId(), salary);
                reply(user.getTelegramId(), "Зарплата успешно добавлена. Идём в главное меню", removeKeyboard());
                forceUserToMainMenu(user);
            } catch (NumberFormatException e) {
                reply(
                        user.getTelegramId(),
                        "Не удалось прочитать указанное значение. Если указываешь десятичное значение, пиши их после точки (не запятой)",
                        mainMenuButton()
                );
            }
        }

    }

}

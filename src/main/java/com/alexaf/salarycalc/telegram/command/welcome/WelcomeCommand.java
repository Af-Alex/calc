package com.alexaf.salarycalc.telegram.command.welcome;

import com.alexaf.salarycalc.telegram.command.abstracts.SendCommand;
import com.alexaf.salarycalc.telegram.command.salary.AddSalaryCommand;
import com.alexaf.salarycalc.telegram.statics.Button;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.user.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.alexaf.salarycalc.telegram.statics.KeyboardFactory.removeKeyboard;

// todo: добавить ещё команд со статусами Welcome1, 2, 3 для продвижения по цепочке первого знакомства (зарплата, добавление цели, расчёт дохода)
@Component
public class WelcomeCommand extends SendCommand {

    private final AddSalaryCommand addSalaryCommand;

    @Autowired
    public WelcomeCommand(AddSalaryCommand addSalaryCommand) {
        super(ChatState.WELCOME);
        this.addSalaryCommand = addSalaryCommand;
    }

    @Override
    public void execute(Update update, UserDto user) {
        sender.send("Привет! Я помогу тебе распределять свои доходы по разным счетам, чтобы ты накопил на свою мечту", user);
        reply(user.getTelegramId(), "Для начала разберёмся с заработной платой. Из неё будут проводиться расчёты", removeKeyboard(), ChatState.ADD_SALARY);

        update.getMessage().setText(Button.ADD.text); // имитация выбора пользователем команды "Добавить зарплату"
        addSalaryCommand.execute(update, user);
    }

}

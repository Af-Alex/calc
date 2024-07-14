package com.alexaf.salarycalc.telegram.command;

import com.alexaf.salarycalc.user.UserDto;
import org.telegram.telegrambots.meta.api.objects.Update;

@FunctionalInterface
public interface Command {
    void execute(Update update, UserDto user);
}

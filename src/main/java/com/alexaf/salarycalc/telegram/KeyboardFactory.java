package com.alexaf.salarycalc.telegram;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.alexaf.salarycalc.telegram.CountAction.GET_DEFAULTS;

public class KeyboardFactory {

    public static final KeyboardRow TO_START_ROW = new KeyboardRow(List.of(new KeyboardButton("В начало")));
    public static final KeyboardRow GET_DEFAULTS_ROW = new KeyboardRow(List.of(new KeyboardButton(GET_DEFAULTS.getCyrillic())));

    public static ReplyKeyboard getActions() {
        List<KeyboardRow> rows = new ArrayList<>();
        // TODO: refactor this
//        for (CountAction action : CountAction.values()) {
//            KeyboardRow row = new KeyboardRow();
//            row.add(new KeyboardButton(action.getCyrillic()));
//            rows.add(row);
//        }
        rows.add(new KeyboardRow(List.of(new KeyboardButton(CountAction.COUNT.getCyrillic()))));
        return getDefaultReply(rows);
    }

    public static ReplyKeyboard getToStart() {
        return getDefaultReply(Collections.emptyList());
    }

    private static ReplyKeyboard getDefaultReply(List<KeyboardRow> rows) {
        rows.add(GET_DEFAULTS_ROW);
        rows.add(TO_START_ROW);

        var reply = new ReplyKeyboardMarkup(rows);
        reply.setResizeKeyboard(true);
        reply.setOneTimeKeyboard(true);
        reply.setKeyboard(rows);
        return reply;
    }

}

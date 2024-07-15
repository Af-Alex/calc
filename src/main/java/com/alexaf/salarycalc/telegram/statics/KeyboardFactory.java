package com.alexaf.salarycalc.telegram.statics;

import org.springframework.data.util.Pair;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.alexaf.salarycalc.telegram.statics.Button.MAIN_MENU;
import static java.util.Objects.isNull;


public class KeyboardFactory {

    public static ReplyKeyboard removeKeyboard() {
        return new ReplyKeyboardRemove(true);
    }

    public static ReplyKeyboard mainMenuKeyboard() {
        List<KeyboardRow> rows = new ArrayList<>(2);
        KeyboardRow row = new KeyboardRow();

        for (Button button : ChatState.MAIN_MENU.getButtons()) {
            row.add(new KeyboardButton(button.text));
        }

        rows.add(row);

        return defaultReply(rows);
    }

    public static ReplyKeyboard getKeyboard(ChatState chatState, boolean addMainMenu) {
        if (chatState.getButtons() == null) {
            return removeKeyboard();
        }
        var rows = getButtonsRows(chatState);

        if (addMainMenu && !Arrays.asList(chatState.getButtons()).contains(MAIN_MENU))
            rows.addLast(new KeyboardRow(new KeyboardButton(MAIN_MENU.text)));

        return defaultReply(rows);
    }

    public static ReplyKeyboard getKeyboard(ChatState chatState) {
        return getKeyboard(chatState, false);
    }

    public static ReplyKeyboard mainMenuButton() {
        return defaultReply(List.of(new KeyboardRow(MAIN_MENU.text)));
    }

    private static ReplyKeyboard defaultReply(List<KeyboardRow> rows) {
        return new ReplyKeyboardMarkup(
                rows,
                true,
                false,
                true,
                null,
                null
        );
    }

    private static LinkedList<KeyboardRow> getButtonsRows(ChatState chatState) {
        if (isNull(chatState.getButtons()))
            return new LinkedList<>();

        var rows = new LinkedList<KeyboardRow>();

        if (chatState.equals(ChatState.MAIN_MENU))
            rows.add(new KeyboardRow(new KeyboardButton(MAIN_MENU.text)));

        var currentRow = new KeyboardRow();

        for (Button button : chatState.getButtons()) {
            currentRow.add(new KeyboardButton(button.text));
            if (currentRow.size() == 2) { // количество кнопок в одном ряду
                rows.add(currentRow);
                currentRow = new KeyboardRow();
            }
        }

        // Добавить последнюю строку, если число кнопок нечётное
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        return rows;
    }

    public static LinkedList<InlineKeyboardRow> getInlineButtonsRows(Iterable<Pair<String, String>> pairs) {
        var rows = new LinkedList<InlineKeyboardRow>();
        var currentRow = new InlineKeyboardRow();

        for (var pair : pairs) {
            var button = new InlineKeyboardButton(pair.getFirst());
            button.setCallbackData(pair.getSecond());
            currentRow.add(button);
            if (currentRow.size() == 2) { // количество кнопок в одном ряду
                rows.add(currentRow);
                currentRow = new InlineKeyboardRow();
            }
        }

        // Добавить последнюю строку, если число кнопок нечётное
        if (!currentRow.isEmpty()) {
            rows.add(currentRow);
        }

        return rows;
    }

}

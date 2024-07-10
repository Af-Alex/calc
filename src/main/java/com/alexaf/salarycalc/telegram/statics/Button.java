package com.alexaf.salarycalc.telegram.statics;

import java.util.Arrays;

import static java.lang.String.format;

public enum Button {
    MAIN_MENU("Главное меню"),
    GOALS("Цели"),
    SALARY("Зарплата"),
    CONTRIBUTION("Доход"),
    ADD("Добавить"),
    REMOVE("Удалить"),
    GET("Получить информацию"),
    UPDATE("Обновить")
;
    public final String text;

    Button(String text) {
        this.text = text;
    }

    public static final Button[] CRUD_BUTTONS = new Button[]{GET, ADD, UPDATE, REMOVE};
    public static final Button[] MAIN_MENU_BUTTON = new Button[]{MAIN_MENU};

    public static Button getByText(String text) {
        return Arrays.stream(values())
                .filter(button -> button.text.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format("Не найдено кнопки с текстом [%s]", text)));
    }
}

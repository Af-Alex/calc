package com.alexaf.salarycalc.telegram;

import lombok.Getter;

@Getter
public enum Buttons {
    ADD_INCOME("Добавить доход"),
    GET_GOALS("Посмотреть свои цели"),
    ADD_GOAL("Добавить цель"),
    ADD_SALARY("Указать зарплату"),
;
    private final String cyrillic;

    Buttons(String cyrillic) {
        this.cyrillic = cyrillic;
    }

}

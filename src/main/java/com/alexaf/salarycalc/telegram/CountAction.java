package com.alexaf.salarycalc.telegram;

import lombok.Getter;

@Getter
public enum CountAction {
    COUNT("Посчитать расходы"),
    GET_DEFAULTS("Узнать стандартные значения");

    private final String cyrillic;

    CountAction(String cyrillic) {
        this.cyrillic = cyrillic;
    }

}

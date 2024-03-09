package com.alexaf.salarycalc.telegram;

import lombok.Getter;

public enum CountAction {
    COUNT("Посчитать расходы");

    @Getter
    private final String cyrillic;
    
    CountAction(String cyrillic) {
        this.cyrillic = cyrillic;
    }
    
}

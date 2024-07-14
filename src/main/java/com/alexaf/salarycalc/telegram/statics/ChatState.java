package com.alexaf.salarycalc.telegram.statics;

import lombok.Getter;

import static com.alexaf.salarycalc.telegram.statics.Button.ACCEPT;
import static com.alexaf.salarycalc.telegram.statics.Button.CONTRIBUTION;
import static com.alexaf.salarycalc.telegram.statics.Button.CRUD_BUTTONS;
import static com.alexaf.salarycalc.telegram.statics.Button.GOALS;
import static com.alexaf.salarycalc.telegram.statics.Button.HELP;
import static com.alexaf.salarycalc.telegram.statics.Button.MAIN_MENU_BUTTON;
import static com.alexaf.salarycalc.telegram.statics.Button.ONE;
import static com.alexaf.salarycalc.telegram.statics.Button.REJECT;
import static com.alexaf.salarycalc.telegram.statics.Button.SALARY;
import static com.alexaf.salarycalc.telegram.statics.Button.TWO;

@Getter
public enum ChatState {
    // Онбординг
    WELCOME(null),

    // Основные команды
    MAIN_MENU(new Button[]{CONTRIBUTION, GOALS, SALARY}),
    STOPPED_BOT(null),

    // Зарплата
    MANAGE_SALARY(CRUD_BUTTONS),
    ADD_SALARY(MAIN_MENU_BUTTON),
    GET_SALARY(MAIN_MENU_BUTTON),
    DELETE_SALARY(MAIN_MENU_BUTTON),

    // Цели
    MANAGE_GOAL(CRUD_BUTTONS),
    ADD_GOAL_TYPE(new Button[]{ONE, TWO, Button.MAIN_MENU}),
    ADD_GOAL_NAME(new Button[]{HELP, Button.MAIN_MENU}),
    ADD_GOAL_MONTHLY_AMOUNT(new Button[]{HELP, Button.MAIN_MENU}),
    ADD_GOAL_PRIORITY(new Button[]{HELP, Button.MAIN_MENU}),
    ADD_GOAL_SAVE_CHECK(new Button[]{ACCEPT, REJECT}),
    GET_GOAL(MAIN_MENU_BUTTON),
    DELETE_GOAL(MAIN_MENU_BUTTON),
    UPDATE_GOAL(MAIN_MENU_BUTTON),

    // Поступления
    MANAGE_CONTRIBUTION(CRUD_BUTTONS),
    ADD_CONTRIBUTION(new Button[]{HELP, Button.MAIN_MENU}),
    GET_CONTRIBUTION(MAIN_MENU_BUTTON),
    DELETE_CONTRIBUTION(MAIN_MENU_BUTTON),
    UPDATE_CONTRIBUTION(MAIN_MENU_BUTTON)
    ;

    private final Button[] buttons;

    ChatState(Button[] buttons) {
        this.buttons = buttons;
    }
}

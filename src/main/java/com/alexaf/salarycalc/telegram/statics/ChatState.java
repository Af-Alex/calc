package com.alexaf.salarycalc.telegram.statics;

import lombok.Getter;

import static com.alexaf.salarycalc.telegram.statics.Button.CONTRIBUTION;
import static com.alexaf.salarycalc.telegram.statics.Button.CRUD_BUTTONS;
import static com.alexaf.salarycalc.telegram.statics.Button.GOALS;
import static com.alexaf.salarycalc.telegram.statics.Button.MAIN_MENU_BUTTON;
import static com.alexaf.salarycalc.telegram.statics.Button.SALARY;

@Getter
public enum ChatState {
    WELCOME(null),
    MAIN_MENU(new Button[]{CONTRIBUTION, GOALS, SALARY}),
    STOPPED_BOT(null),

    MANAGE_SALARY(CRUD_BUTTONS),
    ADD_SALARY(MAIN_MENU_BUTTON),
    GET_SALARY(MAIN_MENU_BUTTON),
    DELETE_SALARY(MAIN_MENU_BUTTON),

    MANAGE_GOAL(CRUD_BUTTONS),
    ADD_GOAL(MAIN_MENU_BUTTON),
    GET_GOAL(MAIN_MENU_BUTTON),
    DELETE_GOAL(MAIN_MENU_BUTTON),
    UPDATE_GOAL(MAIN_MENU_BUTTON),

    MANAGE_CONTRIBUTION(CRUD_BUTTONS),
    ADD_CONTRIBUTION(MAIN_MENU_BUTTON),
    GET_CONTRIBUTION(MAIN_MENU_BUTTON),
    DELETE_CONTRIBUTION(MAIN_MENU_BUTTON),
    UPDATE_CONTRIBUTION(MAIN_MENU_BUTTON)
    ;

    private final Button[] buttons;

    ChatState(Button[] buttons) {
        this.buttons = buttons;
    }
}

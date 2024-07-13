package com.alexaf.salarycalc.goal;

import com.alexaf.salarycalc.telegram.command.goal.add.AddGoalTypeCommand;
import lombok.Getter;

@Getter
public enum GoalType {
    FIXED_AMOUNT_WITH_DEADLINE("Откладывать фиксированное значение от зарплаты до наступления определённой даты"),
    FIXED_AMOUNT_WITHOUT_DEADLINE("Откладывать фиксированное значение от зарплаты"),
    MONTHLY_PERCENTAGE_WITH_TOTAL("Откладывать процент от зарплаты, пока не накопится сумма"),
    MONTHLY_PERCENTAGE_WITH_DEADLINE("Откладывать процент от зарплаты до наступления определённой даты"),
    MONTHLY_PERCENTAGE_WITHOUT_DEADLINE("Всегда откладывать процент от зарплаты");

    /** !!!Порядок имеет значение!!!
     *  @see AddGoalTypeCommand
     */
    public static final GoalType[] AVAILABLE_TYPES = new GoalType[]{

            FIXED_AMOUNT_WITHOUT_DEADLINE,
            MONTHLY_PERCENTAGE_WITHOUT_DEADLINE
    };

    private final String text;

    GoalType(String text) {this.text = text;}
}

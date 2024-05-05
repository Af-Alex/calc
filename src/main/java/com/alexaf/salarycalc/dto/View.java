package com.alexaf.salarycalc.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record View(
        @JsonProperty("% за обучение") Double kataTax,
        @JsonProperty("подушка безопасности") Double airbag,
        @JsonProperty("обязательные расходы") Double mandatory,
        @JsonProperty("оплата квартиры") Double apartment,
        @JsonProperty("обязательные расходы Юли") Double juliaNeeds,
        @JsonProperty("долг Юли") Double juliaLoan,
        @JsonProperty("сумма перевода на совместный счёт") Double sum,
        @JsonProperty("остаток") Double rest,
        @JsonProperty("Сумма всех значений для проверки") Double all
) {
    public View(double kataTax, double airbag, double mandatory, double apartment,
                double juliaNeeds, double juliaLoan, double sum, double rest) {
        this(kataTax, airbag, mandatory, apartment, juliaNeeds, juliaLoan, sum, rest, kataTax + airbag + apartment + sum + rest);
    }
}
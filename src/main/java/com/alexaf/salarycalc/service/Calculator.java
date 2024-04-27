package com.alexaf.salarycalc.service;

import com.alexaf.salarycalc.dto.Defaults;
import com.alexaf.salarycalc.dto.View;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.alexaf.salarycalc.utils.CalcUtils.percentOf;
import static com.alexaf.salarycalc.utils.CalcUtils.round;

@Slf4j
@Service
public class Calculator {

    private final double monthSalary;
    private final double kataTaxS;
    private final double airbagS;
    private final double mandatoryS;
    private final double apartmentS;
    private final double juliaNeedsS;
    private final double juliaLoanS;

    public Calculator(@Value("${calc.service.kata-tax:0.17}") double kataTax,
                      @Value("${calc.service.month-salary:180000}") double monthSalary,
                      @Value("${calc.service.airbag:30000}") double airbag,
                      @Value("${calc.service.mandatory:46000}") double mandatory,
                      @Value("${calc.service.apartment:29000}") double apartment,
                      @Value("${calc.service.julia-needs:13000}") double juliaNeeds,
                      @Value("${calc.service.julia-loan:8300}") double juliaLoan
    ) {
        // Инициализируем значения, которые в конфиге являются процентами
        this.monthSalary = monthSalary;
        this.kataTaxS = kataTax * monthSalary;
        this.airbagS = 0 < airbag && airbag < 1 ? airbag * monthSalary : airbag;
        this.mandatoryS = 0 < mandatory && mandatory < 1 ? mandatory * monthSalary : mandatory;
        this.apartmentS = 0 < apartment && apartment < 1 ? apartment * monthSalary : apartment;
        this.juliaNeedsS = 0 < juliaNeeds && juliaNeeds < 1 ? juliaNeeds * monthSalary : juliaNeeds;
        this.juliaLoanS = 0 < juliaLoan && juliaLoan < 1 ? juliaLoan * monthSalary : juliaLoan;
    }


    public View calculate(Double salary) {
        double salaryP = percentOf(salary, monthSalary);
        double kataTax = round(kataTaxS * salaryP, 2);
        double airbag = round(airbagS * salaryP, 0);
        double apartment = round(apartmentS * salaryP, 0);
        double mandatory = round(mandatoryS * salaryP, 0);
        double juliaNeeds = round(juliaNeedsS * salaryP, 0);
        double juliaLoan = round(juliaLoanS * salaryP, 0);
        double rest = round((salary - kataTax - airbag - mandatory - apartment - juliaNeeds - juliaLoan), 2) / 2; // 2 - количество людей, на которых делится остаток после обязательных трат
        double sum = mandatory + juliaNeeds + juliaLoan + rest;

        return new View(
                kataTax,
                airbag,
                mandatory,
                apartment,
                juliaNeeds,
                juliaLoan,
                sum,
                rest
        );
    }

    public Defaults getDefaults() {
        return new Defaults(kataTaxS, airbagS, mandatoryS, juliaNeedsS, juliaLoanS, apartmentS);
    }

}

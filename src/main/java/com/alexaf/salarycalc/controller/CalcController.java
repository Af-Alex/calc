package com.alexaf.salarycalc.controller;

import com.alexaf.salarycalc.dto.Defaults;
import com.alexaf.salarycalc.dto.View;
import com.alexaf.salarycalc.model.CalculationRepository;
import com.alexaf.salarycalc.service.Calculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(
        value = "/calc",
        produces = "application/json;charset=UTF-8"
)
public class CalcController {

    private final Calculator calculator;

    private final CalculationRepository calculationRepository;

    @Autowired
    public CalcController(Calculator calculator,
                          @Autowired(required = false) CalculationRepository calculationRepository) {
        this.calculator = calculator;
        this.calculationRepository = calculationRepository;
    }

    @GetMapping("/count")
    public View calculate(@RequestParam Double zp) {
        log.debug("Запустили расчёт...");
        return calculator.calculate(zp);
    }

    @GetMapping("/defaults")
    public Defaults getDefaults() {
        return calculator.getDefaults();
    }

    @GetMapping("/kataPercent")
    public Double getKataPercent() throws HttpRequestMethodNotSupportedException {
        if (calculationRepository == null) {
            throw new HttpRequestMethodNotSupportedException("Доступ к Excel-файлу невозможен");
        }
        return calculationRepository.getKataPercent();
    }

}

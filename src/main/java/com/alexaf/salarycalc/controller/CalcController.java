package com.alexaf.salarycalc.controller;

import com.alexaf.salarycalc.dto.Defaults;
import com.alexaf.salarycalc.dto.View;
import com.alexaf.salarycalc.service.Calculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/calc",
        produces = "application/json;charset=UTF-8"
)
public class CalcController {

    private final Calculator calculator;


    @GetMapping("/count")
    public View calculate(@RequestParam Double zp) {
        log.debug("Запустили расчёт...");
        return calculator.calculate(zp);
    }

    @GetMapping("/defaults")
    public Defaults getDefaults() {
        return calculator.getDefaults();
    }

}

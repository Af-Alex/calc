package com.alexaf.salarycalc.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import static java.math.RoundingMode.HALF_DOWN;

@Slf4j
public class CalcUtils {

    public static BigDecimal percentOf(BigDecimal willBePercent, BigDecimal percent100) {
        var result = willBePercent.divide(percent100, 4, HALF_DOWN);
        log.debug("{} составляет {}% от {}", willBePercent, result.multiply(new BigDecimal(100)), percent100);
        return result;
    }

}

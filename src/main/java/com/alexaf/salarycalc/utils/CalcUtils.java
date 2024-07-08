package com.alexaf.salarycalc.utils;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public class CalcUtils {

    /**
     * @return Процент, который составляет первый аргумент от второго
     */
    public static double percentOf(double willBePercent, double second) {
        var res = willBePercent / second;
        log.debug("Расчёт произведён: a={}, b={}, res={}", willBePercent, second, res);
        return res;
    }

    /**
     * @param number значение для округления
     * @param count  количество цифр после запятой
     */
    public static double round(double number, int count) {
        var scale = Math.pow(10, count);
        return Math.ceil(number * scale) / scale;
    }

    public static BigDecimal percentOf(BigDecimal willBePercent, BigDecimal percent100) {
        return willBePercent.divide(percent100, new MathContext(2, RoundingMode.HALF_DOWN));
    }

}

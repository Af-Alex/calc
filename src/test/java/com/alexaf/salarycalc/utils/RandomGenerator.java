package com.alexaf.salarycalc.utils;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RandomGenerator {

    private static final Random rand = new Random();

    public static int intGen() {
        return rand.nextInt(1000);
    }

    public static long longGen() {
        return rand.nextLong(Long.MAX_VALUE);
    }

    public static BigDecimal bdGen() {
        return BigDecimal.valueOf(rand.nextDouble(1_000_000.00)).setScale(2, RoundingMode.HALF_UP);
    }

    public static String strGen() {
        return strGen(10);
    }

    public static String strGen(int count) {
        return RandomStringUtils.randomAlphabetic(count);
    }
}

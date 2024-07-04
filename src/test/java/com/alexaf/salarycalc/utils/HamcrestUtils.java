package com.alexaf.salarycalc.utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class HamcrestUtils {

    public static <T extends Object> Matcher<T> isNullable(T expected) {
        return expected == null ? (Matcher<T>) nullValue() : is(expected);
    }

    public static Matcher<? extends Object> isNullableLDT(LocalDateTime expected) {
        return expected == null ? nullValue() : new TypeSafeMatcher<LocalDateTime>() {
            @Override
            protected boolean matchesSafely(LocalDateTime real) {
                return expected.withNano(0).equals(real.withNano(0));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Сравнить LocalDateTime.withNano(0)");
            }
        };
    }

}

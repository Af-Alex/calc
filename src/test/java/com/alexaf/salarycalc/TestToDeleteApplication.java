package com.alexaf.salarycalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestToDeleteApplication {

    public static void main(String[] args) {
        SpringApplication.from(SalaryCalculatorApplication::main).with(TestToDeleteApplication.class).run(args);
    }

}

package com.alexaf.salarycalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SalaryCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalaryCalculatorApplication.class, args);
    }

}

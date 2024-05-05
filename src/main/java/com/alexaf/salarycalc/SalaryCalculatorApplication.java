package com.alexaf.salarycalc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = {
                "com.alexaf.salarycalc.**.repository"
        })
@EnableTransactionManagement
@EnableScheduling
public class SalaryCalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalaryCalculatorApplication.class, args);
    }

}

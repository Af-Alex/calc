package com.alexaf.salarycalc;

import com.alexaf.salarycalc.telegram.SalaryBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = {
                "com.alexaf.salarycalc.**.repository"
        })
@EnableTransactionManagement
public class SalaryCalculatorApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SalaryCalculatorApplication.class, args);
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(ctx.getBean("salaryBot", SalaryBot.class));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}

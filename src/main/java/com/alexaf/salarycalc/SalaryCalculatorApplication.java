package com.alexaf.salarycalc;

import com.alexaf.salarycalc.telegram.SalaryBot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
@EnableJpaRepositories(
        basePackages = {
                "com.alexaf.salarycalc.**.repository"
        })
@EnableTransactionManagement
public class SalaryCalculatorApplication {

    public static void main(String[] args) {
        var ctx = SpringApplication.run(SalaryCalculatorApplication.class, args);
        try {
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            // Register your newly created AbilityBot
            botsApplication.registerBot(ctx.getEnvironment().getProperty("telegram.bot-token"), ctx.getBean(SalaryBot.class));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Telegram bot started");
    }

}

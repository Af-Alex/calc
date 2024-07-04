package com.alexaf.salarycalc.telegram;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
@ConditionalOnBean(TelegramConfig.class)
public class BotRegistrar {

    @EventListener
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        try {
            var bot = event.getApplicationContext().getBean(SalaryBot.class);
            var token = event.getApplicationContext().getEnvironment().getProperty("telegram.bot-token");
            TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
            // Register your newly created AbilityBot
            botsApplication.registerBot(token, bot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Telegram bot started");
    }

}

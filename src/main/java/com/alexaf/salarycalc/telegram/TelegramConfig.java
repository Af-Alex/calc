package com.alexaf.salarycalc.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static com.alexaf.salarycalc.telegram.TelegramConfig.TELEGRAM_ENABLED_PROPERTY;

@Configuration
@ConditionalOnProperty(value = TELEGRAM_ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
public class TelegramConfig {

    public static final String TELEGRAM_ENABLED_PROPERTY = "telegram.enabled";

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot-token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

    @Bean
    public SilentSender silentSender(TelegramClient client) {
        return new SilentSender(client);
    }

}

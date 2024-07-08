package com.alexaf.salarycalc.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Slf4j
@ConditionalOnBean(TelegramConfig.class)
public class CalcBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient client;
    public CalcBot(TelegramClient telegramClient) {
        this.client = telegramClient;
    }

    @Override
    @Transactional
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {

        }
        log.debug("Запрос проигнорирован, т.к. не содержит текста");
    }

}

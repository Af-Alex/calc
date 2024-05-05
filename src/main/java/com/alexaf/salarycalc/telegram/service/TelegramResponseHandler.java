package com.alexaf.salarycalc.telegram.service;

import com.alexaf.salarycalc.dto.View;
import com.alexaf.salarycalc.service.Calculator;
import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.telegram.CountAction;
import com.alexaf.salarycalc.telegram.KeyboardFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import static com.alexaf.salarycalc.telegram.ChatState.SELECT_ACTION;
import static com.alexaf.salarycalc.telegram.ChatState.STOPPED_BOT;
import static com.alexaf.salarycalc.telegram.Constants.START_TEXT;
import static com.alexaf.salarycalc.telegram.CountAction.GET_DEFAULTS;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramResponseHandler {

    private final ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private final Calculator calculator;
    private final TelegramService telegramService;
    private final SilentSender sender;


    public void replyToStart(long chatId) {
        promptWithKeyboardForState(chatId, START_TEXT, KeyboardFactory.getActions(), SELECT_ACTION);
    }

    public void replyToSelect(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase(CountAction.COUNT.getCyrillic())) {
            replyToCountAction(chatId, message);
        } else if (message.getText().equalsIgnoreCase("В начало")) {
            replyToStart(chatId);
        } else if (message.getText().equalsIgnoreCase(GET_DEFAULTS.getCyrillic())) {
            replyToGetDefaults(chatId, message);
        } else {
            promptWithKeyboardForState(chatId, "Такого действия нет, выбери из предложенных", KeyboardFactory.getActions(), SELECT_ACTION);
        }
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }

        switch (telegramService.getUserChatState(chatId)) {
            case WELCOME, SELECT_ACTION -> replyToSelect(chatId, message);
            case COUNT_ACTION -> replyToCountAction(chatId, message);
            case TYPE_SALARY_ACTION -> replyToSalaryAnswer(chatId, message);
            case GET_DEFAULTS_ACTION -> replyToGetDefaults(chatId, message);
            default -> unexpectedMessage(chatId);
        }
    }

    private void replyToGetDefaults(long chatId, Message message) {
        SendMessage.SendMessageBuilder<?, ?> smBuilder = SendMessage.builder()
                .chatId(chatId);
        try {
            smBuilder.text(writer.writeValueAsString(calculator.getDefaults()));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            smBuilder.text("При попытке генерации ответа произошла ошибка. Попробуй ещё раз");
            smBuilder.replyMarkup(KeyboardFactory.getActions());
        } finally {
            telegramService.updateChatState(chatId, SELECT_ACTION);
            sender.execute(smBuilder.build());
        }
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("I did not expect that.")
                .build();
        sender.execute(sendMessage);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Type /start to interact again")
                .replyMarkup(new ReplyKeyboardRemove(true))
                .build();
        telegramService.updateChatState(chatId, STOPPED_BOT);
        sender.execute(sendMessage);
    }

    private void replyToCountAction(long chatId, Message message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Введи сумму для расчёта")
                .replyMarkup(new ReplyKeyboardRemove(true))
                .build();
        sender.execute(sendMessage);
        telegramService.updateChatState(chatId, ChatState.TYPE_SALARY_ACTION);
    }

    private void promptWithKeyboardForState(long chatId, String text, ReplyKeyboard buttons, ChatState nextState) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(buttons)
                .build();
        sender.execute(sendMessage);
        telegramService.updateChatState(chatId, nextState);
    }

    private void replyToSalaryAnswer(long chatId, Message message) {
        SendMessage.SendMessageBuilder<?, ?> smBuilder = SendMessage.builder()
                .chatId(chatId);
        try {
            String salary = message.getText().replace(",", "");
            View result = calculator.calculate(Double.parseDouble(salary));
            smBuilder.text("Вот как надо распределить средства:\n" + writer.writeValueAsString(result));
            smBuilder.replyMarkup(KeyboardFactory.getActions());
        } catch (NumberFormatException e) {
            smBuilder.text("Не удалось прочитать значение. Попробуй ещё раз");
            smBuilder.replyMarkup(KeyboardFactory.getToStart());
        } catch (JsonProcessingException e) {
            smBuilder.text("При попытке генерации ответа произошла ошибка. Попробуй ещё раз");
            smBuilder.replyMarkup(KeyboardFactory.getToStart());
        } finally {
            telegramService.updateChatState(chatId, SELECT_ACTION);
            sender.execute(smBuilder.build());
        }
    }

    public boolean userIsActive(Long chatId) {
        // TODO: check if user is active
        return true;
    }
}

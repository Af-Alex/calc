package com.alexaf.salarycalc.telegram;

import com.alexaf.salarycalc.dto.View;
import com.alexaf.salarycalc.service.Calculator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.Map;

import static com.alexaf.salarycalc.telegram.Constants.CHAT_STATES;
import static com.alexaf.salarycalc.telegram.Constants.START_TEXT;
import static com.alexaf.salarycalc.telegram.UserState.SELECT_ACTION;

public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, UserState> chatStates;
    private final ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private final Calculator calculator;

    public ResponseHandler(SilentSender sender, DBContext db, Calculator calculator) {
        this.sender = sender;
        this.calculator = calculator;
        chatStates = db.getMap(CHAT_STATES);
    }

    public void replyToStart(long chatId) {
        promptWithKeyboardForState(chatId, START_TEXT, KeyboardFactory.getActions(), SELECT_ACTION);
    }

    public void replyToSelect(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase(CountAction.COUNT.getCyrillic())) {
            replyToCountAction(chatId, message);
        } else if (message.getText().equalsIgnoreCase("В начало")){
            replyToStart(chatId);
        } else {
            promptWithKeyboardForState(chatId, "Такого действия нет, выбери из предложенных", KeyboardFactory.getActions(), SELECT_ACTION);
        }
    }

    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }

        switch (chatStates.get(chatId)) {
            case SELECT_ACTION -> replyToSelect(chatId, message);
            case COUNT_ACTION -> replyToCountAction(chatId, message);
            case TYPE_SALARY_ACTION -> replyToSalaryAnswer(chatId, message);
            default -> unexpectedMessage(chatId);
        }
    }

    private void unexpectedMessage(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("I did not expect that.");
        sender.execute(sendMessage);
    }

    private void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Type /start to interact again");
        chatStates.remove(chatId);
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
    }

    private void replyToCountAction(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Введи сумму для расчёта");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        sender.execute(sendMessage);
        chatStates.put(chatId, UserState.TYPE_SALARY_ACTION);
    }

    private void promptWithKeyboardForState(long chatId, String text, ReplyKeyboard buttons, UserState nextState) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(buttons);
        sender.execute(sendMessage);
        chatStates.put(chatId, nextState);
    }

    private void replyToSalaryAnswer(long chatId, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        try {
            String salary = message.getText().replace(",", "");
            View result = calculator.calculate(Double.parseDouble(salary));
            sendMessage.setText("Вот как надо распределить средства:\n" + writer.writeValueAsString(result));
            sendMessage.setReplyMarkup(KeyboardFactory.getActions());
            chatStates.put(chatId, SELECT_ACTION);
        } catch (NumberFormatException e) {
            sendMessage.setText("Не удалось прочитать значение. Попробуй ещё раз");
            sendMessage.setReplyMarkup(KeyboardFactory.getToStart());
            chatStates.put(chatId, SELECT_ACTION);
        } catch (JsonProcessingException e) {
            sendMessage.setText("При попытке генерации ответа произошла ошибка. Попробуйте ещё раз");
            sendMessage.setReplyMarkup(KeyboardFactory.getToStart());
            chatStates.put(chatId, SELECT_ACTION);
        } finally {
            sender.execute(sendMessage);
        }
    }

    public boolean userIsActive(Long chatId) {
        return chatStates.containsKey(chatId);
    }
}

package com.alexaf.salarycalc.telegram.service;

import com.alexaf.salarycalc.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor

public class SilentSender {

    private final TelegramClient telegramClient;


    public Optional<Message> send(String message, long id) {
        return doSendMessage(message, id, false);
    }

    public Optional<Message> sendMd(String message, long id) {
        return doSendMessage(message, id, true);
    }

    public Optional<Message> forceReply(String message, long id) {
        SendMessage msg = new SendMessage(Long.toString(id), message);
        ForceReplyKeyboard kb = new ForceReplyKeyboard();
        kb.setForceReply(true);
        kb.setSelective(true);
        msg.setReplyMarkup(kb);

        return execute(msg);
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> Optional<T> execute(Method method) {
        try {
            return Optional.ofNullable(telegramClient.execute(method));
        } catch (TelegramApiException e) {
            log.error("Could not execute bot API method", e);
            return Optional.empty();
        }
    }

    private Optional<Message> doSendMessage(String txt, long groupId, boolean format) {
        SendMessage smsg = new SendMessage(Long.toString(groupId), txt);
        smsg.enableMarkdown(format);

        return execute(smsg);
    }

    public void send(String message, UserDto user) {
        this.send(message, user.getTelegramId());
    }
}

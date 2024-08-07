package com.alexaf.salarycalc.telegram.command.abstracts;

import com.alexaf.salarycalc.telegram.service.SilentSender;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public abstract class SendCommand extends ChatStateCommand {

    protected SilentSender sender;

    public SendCommand(ChatState chatState) {
        super(chatState);
    }

    @Autowired
    private void setSilentSender(SilentSender sender) {
        this.sender = sender;
    }

    protected void reply(Long chatId, String text, ReplyKeyboard buttons, ChatState nextState) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(buttons)
                .build();
        sender.execute(sendMessage);
        userService.updateChatState(chatId, nextState);
    }

    protected void reply(Long chatId, String text, ReplyKeyboard buttons) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .replyMarkup(buttons)
                .build();
        sender.execute(sendMessage);
    }

}

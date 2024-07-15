package com.alexaf.salarycalc.telegram;

import com.alexaf.salarycalc.telegram.command.ChatStateCommandRegistry;
import com.alexaf.salarycalc.telegram.command.Command;
import com.alexaf.salarycalc.telegram.service.SilentSender;
import com.alexaf.salarycalc.telegram.service.TelegramService;
import com.alexaf.salarycalc.telegram.statics.ChatState;
import com.alexaf.salarycalc.telegram.statics.KeyboardFactory;
import com.alexaf.salarycalc.user.UserDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;


@Slf4j
@Component
@ConditionalOnBean(TelegramConfig.class)
public class SalaryBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramService telegramService;
    private final SilentSender sender;
    private final ChatStateCommandRegistry commandRegistry;


    @Autowired
    public SalaryBot(
            SilentSender sender, TelegramService telegramService, ChatStateCommandRegistry commandRegistry, @Value("${telegram.creator-id:371923388}") Long creatorId
    ) {
        this.sender = sender;
        this.commandRegistry = commandRegistry;
        this.telegramService = telegramService;
        this.sender.send("Bot started", creatorId);
    }

    @Override
    @Transactional
    public void consume(Update update) {
        log.debug("Salary bot consumes update: {}", update);
        long millisStarted = System.currentTimeMillis();

        UserDto user = extractUser(update);
        if (!user.isActive())
            sender.send("Аккаунт отключен", user.getTelegramId());

        Command command;

        try {
            command = commandRegistry.getCommand(user.getChatState());
        } catch (IllegalArgumentException e) {
            log.error("Не нашлось команды для пользователя {}", user);
            SendMessage errorMessage = SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .text("Не удалось определить твоё состояние. Ошибка на нашей стороне, скоро поправим!\nA пока - в главное меню")
                    .replyMarkup(KeyboardFactory.mainMenuKeyboard())
                    .build();
            sender.execute(errorMessage);
            telegramService.updateChatState(user, ChatState.MAIN_MENU);
            return;
        }

        try {
            command.execute(update, user);
        } catch (Exception e) {
            log.error("Ошибка выполнения команды", e);
            SendMessage errorMessage = SendMessage.builder()
                    .chatId(user.getTelegramId())
                    .text("Возникла ошибка: " + e.getMessage() + "\nВозвращаемся в главное меню")
                    .replyMarkup(KeyboardFactory.mainMenuKeyboard())
                    .build();
            sender.execute(errorMessage);
            telegramService.updateChatState(user, ChatState.MAIN_MENU);
        }

        long processingTime = System.currentTimeMillis() - millisStarted;
        log.info(format("Processing of update [%s] ended at %s%n---> Processing time: [%d ms] <---%n", update.getUpdateId(), now(), processingTime));
    }

    private UserDto extractUser(Update update) {
        User user = getUser(update);

        return telegramService.find(user)
                .orElseGet(() -> telegramService.registerUser(user));
    }

    private User getUser(Update update) {
        if (update.getMessage() != null)
            return update.getMessage().getFrom();
        if (update.getCallbackQuery() != null)
            return update.getCallbackQuery().getFrom();
        throw new IllegalArgumentException("Не удалось извлечь пользователя из запроса");
    }

}

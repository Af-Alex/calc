package com.alexaf.salarycalc.telegram.service;

import com.alexaf.salarycalc.goal.GoalService;
import com.alexaf.salarycalc.goal.repository.Goal;
import com.alexaf.salarycalc.service.DistributionService;
import com.alexaf.salarycalc.telegram.Buttons;
import com.alexaf.salarycalc.telegram.ChatState;
import com.alexaf.salarycalc.telegram.KeyboardFactory;
import com.alexaf.salarycalc.telegram.SilentSender;
import com.alexaf.salarycalc.telegram.TelegramConfig;
import com.alexaf.salarycalc.user.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.alexaf.salarycalc.telegram.Buttons.GET_GOALS;
import static com.alexaf.salarycalc.telegram.ChatState.SELECT_ACTION;
import static com.alexaf.salarycalc.telegram.ChatState.STOPPED_BOT;
import static com.alexaf.salarycalc.telegram.ChatState.TYPE_SALARY_ACTION;
import static com.alexaf.salarycalc.telegram.Constants.START_TEXT;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(TelegramConfig.class)
public class TelegramResponseHandler {

    private final ObjectWriter writer = new ObjectMapper().writerWithDefaultPrettyPrinter();
    private final DistributionService distributionService;
    private final TelegramService telegramService;
    private final SilentSender sender;
    private final GoalService goalService;
    private final UserService userService;


    public void replyToStart(long chatId) {
        promptWithKeyboardForState(chatId, START_TEXT, KeyboardFactory.getActions(), SELECT_ACTION);
    }

    public void replyToSelect(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase(Buttons.ADD_INCOME.getCyrillic())) {
            replyToAddIncomeAction(chatId, message);
        } else if (message.getText().equalsIgnoreCase("В начало")) {
            replyToStart(chatId);
        } else if (message.getText().equalsIgnoreCase(GET_GOALS.getCyrillic())) {
            replyToGetGoalsAction(chatId, message);
        } else {
            promptWithKeyboardForState(chatId, "Такого действия нет, выбери из предложенных", KeyboardFactory.getActions(), SELECT_ACTION);
        }
    }

    @Transactional
    public void replyToButtons(long chatId, Message message) {
        if (message.getText().equalsIgnoreCase("/stop")) {
            stopChat(chatId);
        }

        switch (telegramService.getUserChatState(chatId)) {
            case WELCOME, SELECT_ACTION -> replyToSelect(chatId, message);
            case ADD_INCOME_ACTION -> replyToAddIncomeAction(chatId, message);
            case TYPE_INCOME_ACTION -> replyToTypeIncomeAction(chatId, message);
            case GET_GOALS_ACTION -> replyToGetGoalsAction(chatId, message);
            case ADD_SALARY_ACTION -> replyToAddSalary(chatId, message);
            default -> unexpectedMessage(chatId);
        }
    }

    private void replyToAddSalary(long chatId, Message message) {
        promptWithKeyboardForState(
                chatId,
                "Введи значение зарплаты. Она будет использоваться для твоих расчётов",
                new ReplyKeyboardRemove(true),
                TYPE_SALARY_ACTION
        );
    }

    private void replyToGetGoalsAction(long chatId, Message message) {
        SendMessage.SendMessageBuilder<?, ?> smBuilder = SendMessage.builder()
                .chatId(chatId);
        try {
            List<Goal> goals = goalService.findActiveByUserIdSortByPriority(userService.getByTelegramId(chatId).getId());
            smBuilder.text(writer.writeValueAsString(goals.stream().collect(Collectors.toMap(
                    Goal::getName,
                    goal -> switch (goal.getType()) {
                        case FIXED_AMOUNT_WITHOUT_DEADLINE -> goal.getMonthlyAmount().toString();
                        case MONTHLY_PERCENTAGE_WITHOUT_DEADLINE -> goal.getMonthlyAmount() + "%";
                        default -> "Не реализовано";
                    }
            ))));
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

    private void replyToAddIncomeAction(long chatId, Message message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text("Введи сумму для расчёта")
                .replyMarkup(new ReplyKeyboardRemove(true))
                .build();
        sender.execute(sendMessage);
        telegramService.updateChatState(chatId, ChatState.TYPE_INCOME_ACTION);
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

    private void replyToTypeIncomeAction(long chatId, Message message) {
        SendMessage.SendMessageBuilder<?, ?> smBuilder = SendMessage.builder()
                .chatId(chatId);
        try {
            String salary = message.getText().replace(",", ".");
            Map<String, BigDecimal> result = distributionService.distributeIncome(chatId, new BigDecimal(salary));

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

package com.alexaf.salarycalc.telegram;

import com.alexaf.salarycalc.telegram.service.TelegramService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;


@Slf4j
@Component
@ConditionalOnBean(TelegramConfig.class)
public class SalaryBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramService telegramService;
    private final Long creatorId;
    private final SilentSender sender;

    @Autowired
    public SalaryBot(
            SilentSender silent,
            TelegramService telegramService,
            @Value("${telegram.creator-id:371923388}") Long creatorId
    ) {
        this.sender = silent;
        this.creatorId = creatorId;
        this.telegramService = telegramService;
        silent.send("Bot started", creatorId);
    }

    public long creatorId() {
        return creatorId;
    }

    @Override
    @Transactional
    public void consume(Update update) {
        log.debug("Salary bot consumes update: {}", update);
        long millisStarted = System.currentTimeMillis();

        Stream.of(update)
                .filter(this::checkUserIsActive)
                .map(this::addUserIfNotExists);
        // todo :
//                    .map(this::resolveCommand)
//                    .map(::executeCommand)


        long processingTime = System.currentTimeMillis() - millisStarted;
        log.info(format("Processing of update [%s] ended at %s%n---> Processing time: [%d ms] <---%n", update.getUpdateId(), now(), processingTime));
    }

    private User extractUser(Update update) {
        return ofNullable(update.getMessage().getFrom()).orElseThrow(
                () -> new RuntimeException("Не удалось извлечь пользователя из запроса")
        );
    }


    Update addUserIfNotExists(Update update) {
        User endUser = extractUser(update);

        if (telegramService.existsById(endUser.getId()))
            log.debug("User {} already registered", endUser.getUserName());
        else {
            telegramService.registerUser(endUser);
        }

        return update;
    }

    boolean checkUserIsActive(Update update) {
        User user = extractUser(update);
        if (isNull(user)) {
            return true;
        }

        return user.getId() == creatorId() || telegramService.find(user).orElseGet(() -> telegramService.registerUser(user))
                .isActive();
    }

}

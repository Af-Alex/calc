package com.alexaf.salarycalc.telegram;

import com.alexaf.salarycalc.service.Calculator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.abilitybots.api.bot.BaseAbilityBot;
import org.telegram.telegrambots.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.objects.MessageContext;
import org.telegram.telegrambots.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.abilitybots.api.objects.Reply;
import org.telegram.telegrambots.abilitybots.api.objects.Stats;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.abilitybots.api.util.Pair;
import org.telegram.telegrambots.abilitybots.api.util.Trio;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Comparator.comparingInt;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.telegram.telegrambots.abilitybots.api.objects.MessageContext.newContext;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityMessageCodes.CHECK_INPUT_FAIL;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityMessageCodes.CHECK_LOCALITY_FAIL;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityMessageCodes.CHECK_PRIVACY_FAIL;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.EMPTY_USER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getLocalizedMessage;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getUser;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.isUserMessage;


@Slf4j
@Component
public class SalaryBot extends AbilityBot {

    @Value("${telegram.creator-id:371923388}")
    private Long creatorId;
    private final TelegramResponseHandler telegramResponseHandler;

    public SalaryBot(
            TelegramClient telegramClient,
            @Value("${telegram.bot-name}") String botUsername,
            DBContext dbContext,
            Calculator calculator,
            SilentSender silent) {
        super(telegramClient, botUsername, dbContext);
        this.silent = silent;
        this.telegramResponseHandler = new TelegramResponseHandler(silent, dbContext, calculator);
        this.onRegister();
        log.debug("Salary bot created");
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(Locality.USER)
                .privacy(PUBLIC)
                .action(ctx -> telegramResponseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Reply replyToButtons() {
        BiConsumer<BaseAbilityBot, Update> action = (abilityBot, upd) -> telegramResponseHandler.replyToButtons(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> telegramResponseHandler.userIsActive(getChatId(upd)));
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    @Override
    public void consume(Update update) {
        log.debug("Salary bot consumes update: {}", update);
        long millisStarted = System.currentTimeMillis();

        Stream.of(update)
                .filter(super::checkGlobalFlags)
                .filter(this::checkBlacklist)
                .map(this::addUser)
                .filter(this::filterReply)
                .filter(this::hasUser)
                .map(this::getAbility)
                .filter(this::validateAbility)
                .filter(this::checkPrivacy)
                .filter(this::checkLocality)
                .filter(this::checkInput)
                .filter(this::checkMessageFlags)
                .map(this::getContext)
                .map(this::consumeUpdate)
                .map(this::updateStats)
                .forEach(this::postConsumption);

        // Commit to DB now after all the actions have been dealt
        db.commit();

        long processingTime = System.currentTimeMillis() - millisStarted;
        log.info(format("[%s] Processing of update [%s] ended at %s%n---> Processing time: [%d ms] <---%n", getBotUsername(), update.getUpdateId(), now(), processingTime));
    }

    Update addUser(Update update) {
        User endUser = AbilityUtils.getUser(update);
        if (endUser.equals(EMPTY_USER)) {
            // Can't add an empty user, return the update as is
            return update;
        }

        users().compute(endUser.getId(), (id, user) -> {
            if (user == null) {
                updateUserId(user, endUser);
                return endUser;
            }

            if (!user.equals(endUser)) {
                updateUserId(user, endUser);
                return endUser;
            }

            return user;
        });

        return update;
    }

    private void postConsumption(Pair<MessageContext, Ability> pair) {
        ofNullable(pair.b().postAction())
                .ifPresent(consumer -> consumer.accept(pair.a()));
    }

    private void updateUserId(User oldUser, User newUser) {
        if (oldUser != null && oldUser.getUserName() != null) {
            // Remove old username -> ID
            userIds().remove(oldUser.getUserName());
        }

        if (newUser.getUserName() != null) {
            // Add new mapping with the new username
            userIds().put(newUser.getUserName().toLowerCase(), newUser.getId());
        }
    }

    boolean filterReply(Update update) {
        return getReplies().stream()
                .filter(reply -> runSilently(() -> reply.isOkFor(update), reply.name()))
                .map(reply -> runSilently(() -> {
                    reply.actOn(this, update);
                    updateReplyStats(reply);
                    return false;
                }, reply.name()))
                .reduce(true, Boolean::logicalAnd);
    }

    boolean runSilently(Callable<Boolean> callable, String name) {
        try {
            return callable.call();
        } catch(Exception ex) {
            String msg = format("Reply [%s] failed to check for conditions. " +
                    "Make sure you're safeguarding against all possible updates.", name);
            if (log.isDebugEnabled()) {
                log.error(msg, ex);
            } else {
                log.error(msg);
            }
        }
        return false;
    }

    Pair<MessageContext, Ability> updateStats(Pair<MessageContext, Ability> pair) {
        Ability ab = pair.b();
        if (ab.statsEnabled()) {
            updateStats(pair.b().name());
        }
        return pair;
    }

    private void updateReplyStats(Reply reply) {
        if (reply.statsEnabled()) {
            updateStats(reply.name());
        }
    }

    void updateStats(String name) {
        Stats statsObj = getStats().get(name);
        statsObj.hit();
        getStats().put(name, statsObj);
    }

    private boolean hasUser(Update update) {
        // Valid updates without users should return an empty user
        // Updates that are not recognized by the getUser method will throw an exception
        return !AbilityUtils.getUser(update).equals(EMPTY_USER);
    }

    Trio<Update, Ability, String[]> getAbility(Update update) {
        // Handle updates without messages
        // Passing through this function means that the global flags have passed
        Message msg = update.getMessage();
        if (!update.hasMessage() || !msg.hasText())
            return Trio.of(update, getAbilities().get(DEFAULT), new String[]{});

        Ability ability;
        String[] tokens;
        if (allowContinuousText()) {
            String abName = getAbilities().keySet().stream()
                    .filter(name -> msg.getText().startsWith(format("%s%s", getCommandPrefix(), name)))
                    .max(comparingInt(String::length))
                    .orElse(DEFAULT);
            tokens = msg.getText()
                    .replaceFirst(getCommandPrefix() + abName, "")
                    .split(getCommandRegexSplit());
            ability = getAbilities().get(abName);
        } else {
            tokens = msg.getText().split(getCommandRegexSplit());
            if (tokens[0].startsWith(getCommandPrefix())) {
                String abilityToken = stripBotUsername(tokens[0].substring(1)).toLowerCase();
                ability = getAbilities().get(abilityToken);
                tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
            } else {
                ability = getAbilities().get(DEFAULT);
            }
        }
        return Trio.of(update, ability, tokens);
    }

    private String stripBotUsername(String token) {
        return compile(format("@%s", getBotUsername()), CASE_INSENSITIVE)
                .matcher(token)
                .replaceAll("");
    }

    boolean validateAbility(Trio<Update, Ability, String[]> trio) {
        return trio.b() != null;
    }

    boolean checkBlacklist(Update update) {
        User user = getUser(update);
        if (isNull(user)) {
            return true;
        }

        long id = user.getId();
        return id == creatorId() || !blacklist().contains(id);
    }

    boolean checkPrivacy(Trio<Update, Ability, String[]> trio) {
        Update update = trio.a();
        User user = AbilityUtils.getUser(update);
        Privacy privacy;
        long id = user.getId();

        privacy = getPrivacy(update, id);

        boolean isOk = privacy.compareTo(trio.b().privacy()) >= 0;

        if (!isOk)
            silent.send(
                    getLocalizedMessage(
                            CHECK_PRIVACY_FAIL,
                            AbilityUtils.getUser(trio.a()).getLanguageCode()),
                    getChatId(trio.a()));
        return isOk;
    }

    boolean checkLocality(Trio<Update, Ability, String[]> trio) {
        Update update = trio.a();
        Locality locality = isUserMessage(update) ? Locality.USER : Locality.GROUP;
        Locality abilityLocality = trio.b().locality();

        boolean isOk = abilityLocality == Locality.ALL || locality == abilityLocality;

        if (!isOk)
            silent.send(
                    getLocalizedMessage(
                            CHECK_LOCALITY_FAIL,
                            AbilityUtils.getUser(trio.a()).getLanguageCode(),
                            abilityLocality.toString().toLowerCase()),
                    getChatId(trio.a()));
        return isOk;
    }

    boolean checkInput(Trio<Update, Ability, String[]> trio) {
        String[] tokens = trio.c();
        int abilityTokens = trio.b().tokens();

        boolean isOk = abilityTokens == 0 || (tokens.length > 0 && tokens.length == abilityTokens);

        if (!isOk)
            silent.send(
                    getLocalizedMessage(
                            CHECK_INPUT_FAIL,
                            AbilityUtils.getUser(trio.a()).getLanguageCode(),
                            abilityTokens, abilityTokens == 1 ? "input" : "inputs"),
                    getChatId(trio.a()));
        return isOk;
    }

    boolean checkMessageFlags(Trio<Update, Ability, String[]> trio) {
        Ability ability = trio.b();
        Update update = trio.a();

        // The following variable is required to avoid bug #JDK-8044546
        BiFunction<Boolean, Predicate<Update>, Boolean> flagAnd = (flag, nextFlag) -> flag && nextFlag.test(update);
        return ability.flags().stream()
                .reduce(true, flagAnd, Boolean::logicalAnd);
    }

    Pair<MessageContext, Ability> getContext(Trio<Update, Ability, String[]> trio) {
        Update update = trio.a();
        User user = AbilityUtils.getUser(update);

        return Pair.of(newContext(update, user, getChatId(update), this, trio.c()), trio.b());
    }

    Pair<MessageContext, Ability> consumeUpdate(Pair<MessageContext, Ability> pair) {
        pair.b().action().accept(pair.a());
        return pair;
    }

}

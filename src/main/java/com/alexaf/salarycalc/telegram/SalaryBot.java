package com.alexaf.salarycalc.telegram;

import com.alexaf.salarycalc.telegram.copies.Ability;
import com.alexaf.salarycalc.telegram.copies.MessageContext;
import com.alexaf.salarycalc.telegram.copies.Reply;
import com.alexaf.salarycalc.telegram.copies.ReplyCollection;
import com.alexaf.salarycalc.telegram.service.TelegramResponseHandler;
import com.alexaf.salarycalc.telegram.service.TelegramService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.objects.Locality;
import org.telegram.telegrambots.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.abilitybots.api.util.Pair;
import org.telegram.telegrambots.abilitybots.api.util.Trio;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.alexaf.salarycalc.utils.AbilityUtils.checkReturnType;
import static com.alexaf.salarycalc.utils.AbilityUtils.getUser;
import static com.alexaf.salarycalc.utils.AbilityUtils.returnAbility;
import static com.alexaf.salarycalc.utils.AbilityUtils.returnExtension;
import static com.alexaf.salarycalc.utils.AbilityUtils.returnReply;
import static com.alexaf.salarycalc.utils.AbilityUtils.returnReplyCollection;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.compile;
import static org.telegram.telegrambots.abilitybots.api.objects.Privacy.PUBLIC;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.EMPTY_USER;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.getChatId;


@Slf4j
@Component
@ConditionalOnBean(TelegramConfig.class)
public class SalaryBot implements AbilityExtension, LongPollingSingleThreadUpdateConsumer {

    private final TelegramResponseHandler responseHandler;
    private final Long creatorId;
    private final TelegramService telegramService;

    // Ability registry
    private final List<AbilityExtension> extensions = new ArrayList<>();
    @Getter
    private final String botUsername;
    @Getter
    private Map<String, Ability> abilities;

    // Reply registry
    @Getter
    private List<Reply> replies;

    @Autowired
    public SalaryBot(
            SilentSender silent,
            TelegramService telegramService,
            TelegramResponseHandler responseHandler,
            @Value("${telegram.creator-id:371923388}") Long creatorId,
            @Value("${telegram.bot-name}") String botName
    ) {
        this.creatorId = creatorId;
        this.responseHandler = responseHandler;
        this.telegramService = telegramService;
        this.botUsername = botName;
        this.onRegister();
        silent.send("Bot started", creatorId);
    }

    public Ability startBot() {
        return Ability
                .builder()
                .name("start")
                .info(Constants.START_DESCRIPTION)
                .locality(Locality.USER)
                .privacy(PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public void onRegister() {
        registerAbilities();
    }

    public Reply replyToButtons() {
        BiConsumer<SalaryBot, Update> action = (abilityBot, upd) -> responseHandler.replyToButtons(getChatId(upd), upd.getMessage());
        return Reply.of(action, Flag.TEXT, upd -> responseHandler.userIsActive(getChatId(upd)));
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
                .filter(this::checkGlobalFlags)
                .filter(this::checkUserIsActive)
                .map(this::addUser)
                .filter(this::filterReply)
                .filter(this::hasUser)
                .map(this::getAbility)
                .filter(this::validateAbility)
                .filter(this::checkInput)
                .filter(this::checkMessageFlags)
                .map(this::getContext)
                .map(this::consumeUpdate)
                .forEach(this::postConsumption);

        long processingTime = System.currentTimeMillis() - millisStarted;
        log.info(format("Processing of update [%s] ended at %s%n---> Processing time: [%d ms] <---%n", update.getUpdateId(), now(), processingTime));
    }

    private boolean checkGlobalFlags(Update update) {
        return true;
    }

    Update addUser(Update update) {
        User endUser = getUser(update);
        if (endUser.equals(EMPTY_USER)) {
            // Can't add an empty user, return the update as is
            return update;
        }

        if (telegramService.existsById(endUser.getId()))
            log.debug("User {} already registered", endUser.getUserName());
        else {
            telegramService.registerUser(endUser);
        }

        return update;
    }

    private void registerAbilities() {
        try {
            // Collect all classes that implement AbilityExtension declared in the bot
            extensions.addAll(stream(getClass().getMethods())
                    .filter(checkReturnType(AbilityExtension.class))
                    .map(returnExtension(this))
                    .collect(Collectors.toList()));

            // Add the bot itself as it is an AbilityExtension
            extensions.add(this);

            // Extract all abilities from every single extension instance
            abilities = extensions.stream()
                    .flatMap(ext -> Arrays.stream(ext.getClass().getMethods())
                            .filter(checkReturnType(Ability.class))
                            .map(returnAbility(ext))
                    )
                    // Abilities are immutable, build it respectively
                    .collect(ImmutableMap::<String, Ability>builder,
                            (b, a) -> b.put(a.name(), a),
                            (b1, b2) -> b1.putAll(b2.build()))
                    .build();

            // Extract all replies from every single extension instance
            Stream<Reply> extensionReplies = extensions.stream()
                    .flatMap(ext -> stream(ext.getClass().getMethods())
                            .filter(checkReturnType(Reply.class))
                            .map(returnReply(ext)))
                    .flatMap(Reply::stream);

            // Extract all replies from extension instances methods, returning ReplyCollection
            Stream<Reply> extensionCollectionReplies = extensions.stream()
                    .flatMap(extension -> stream(extension.getClass().getMethods())
                            .filter(checkReturnType(ReplyCollection.class))
                            .map(returnReplyCollection(extension))
                            .flatMap(ReplyCollection::stream));

            // Replies can be standalone or attached to abilities, fetch those too
            Stream<Reply> abilityReplies = abilities.values().stream()
                    .flatMap(ability -> ability.replies().stream())
                    .flatMap(Reply::stream);

            // Now create the replies registry (list)
            replies = Stream.of(abilityReplies, extensionReplies, extensionCollectionReplies)
                    .flatMap(replyStream -> replyStream)
                    .collect(
                            ImmutableList::<Reply>builder,
                            ImmutableList.Builder::add,
                            (b1, b2) -> b1.addAll(b2.build()))
                    .build();
        } catch (IllegalStateException e) {
            log.error("Duplicate names found while registering abilities. Make sure that the abilities declared don't clash with the reserved ones.", e);
            throw new RuntimeException(e);
        }
    }


    private void postConsumption(Pair<MessageContext, Ability> pair) {
        ofNullable(pair.b().postAction())
                .ifPresent(consumer -> consumer.accept(pair.a()));
    }

    boolean filterReply(Update update) {
        return getReplies().stream()
                .filter(reply -> runSilently(() -> reply.isOkFor(update), reply.name()))
                .map(reply -> runSilently(() -> {
                    reply.actOn(this, update);
                    return false;
                }, reply.name()))
                .reduce(true, Boolean::logicalAnd);
    }

    boolean runSilently(Callable<Boolean> callable, String name) {
        try {
            return callable.call();
        } catch (Exception ex) {
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

    private boolean hasUser(Update update) {
        // Valid updates without users should return an empty user
        // Updates that are not recognized by the getUser method will throw an exception
        return getUser(update).equals(EMPTY_USER);
    }

    protected boolean allowContinuousText() {
        return false;
    }

    protected String getCommandPrefix() {
        return "/";
    }

    protected String getCommandRegexSplit() {
        return " ";
    }

    Trio<Update, Ability, String[]> getAbility(Update update) {
        // Handle updates without messages
        // Passing through this function means that the global flags have passed
        Message msg = update.getMessage();

        Ability ability;
        String[] tokens;

        tokens = msg.getText().split(getCommandRegexSplit());
        if (tokens[0].startsWith(getCommandPrefix())) {
            String abilityToken = stripBotUsername(tokens[0].substring(1)).toLowerCase();
            ability = getAbilities().get(abilityToken);
            tokens = Arrays.copyOfRange(tokens, 1, tokens.length);
        } else {
            log.error("Сюда мы не должны были попасть... NEED DEFAULT Ability");
            ability = getAbilities().get("DEFAULT");
            tokens = new String[0];
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

    boolean checkUserIsActive(Update update) {
        User user = getUser(update);
        if (isNull(user)) {
            return true;
        }

        long id = user.getId();
        return id == creatorId() || telegramService.find(user).orElseGet(
                () -> telegramService.registerUser(user)
        ).getActive();
    }

    boolean checkInput(Trio<Update, Ability, String[]> trio) {
        String[] tokens = trio.c();
        int abilityTokens = trio.b().tokens();

        boolean isOk = abilityTokens == 0 || (tokens.length > 0 && tokens.length == abilityTokens);

        if (!isOk)
            log.error("Invalid number of tokens. Expected: {}, Actual: {}", abilityTokens, tokens.length);
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
        User user = getUser(update);

        return Pair.of(MessageContext.newContext(update, user, getChatId(update), this, trio.c()), trio.b());
    }

    Pair<MessageContext, Ability> consumeUpdate(Pair<MessageContext, Ability> pair) {
        pair.b().action().accept(pair.a());
        return pair;
    }

}

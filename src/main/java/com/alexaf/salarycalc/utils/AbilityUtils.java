package com.alexaf.salarycalc.utils;

import com.alexaf.salarycalc.telegram.copies.Ability;
import com.alexaf.salarycalc.telegram.copies.Reply;
import com.alexaf.salarycalc.telegram.copies.ReplyCollection;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.abilitybots.api.objects.Flag;
import org.telegram.telegrambots.abilitybots.api.util.AbilityExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.telegram.telegrambots.abilitybots.api.util.AbilityUtils.EMPTY_USER;

@Slf4j
public class AbilityUtils {

    /**
     * @param clazz the type to be tested
     * @return a predicate testing the return type of the method corresponding to the class parameter
     */
    public static Predicate<Method> checkReturnType(Class<?> clazz) {
        return method -> clazz.isAssignableFrom(method.getReturnType());
    }

    /**
     * Invokes the method and retrieves its return {@link Reply}.
     *
     * @param obj a bot or extension that this method is invoked with
     * @return a {@link Function} which returns the {@link Reply} returned by the given method
     */
    public static Function<? super Method, AbilityExtension> returnExtension(Object obj) {
        return method -> {
            try {
                return (AbilityExtension) method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Could not add ability extension", e);
                throw new RuntimeException(e);
            }
        };
    }


    /**
     * Invokes the method and retrieves its return {@link Ability}.
     *
     * @param obj a bot or extension that this method is invoked with
     * @return a {@link Function} which returns the {@link Ability} returned by the given method
     */
    public static Function<? super Method, Ability> returnAbility(Object obj) {
        return method -> {
            try {
                return (Ability) method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Could not add ability", e);
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Invokes the method and retrieves its return {@link Reply}.
     *
     * @param obj a bot or extension that this method is invoked with
     * @return a {@link Function} which returns the {@link Reply} returned by the given method
     */
    public static Function<? super Method, Reply> returnReply(Object obj) {
        return method -> {
            try {
                return (Reply) method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Could not add reply", e);
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Invokes the method and retrieves its return {@link ReplyCollection}.
     *
     * @param obj a bot or extension that this method is invoked with
     * @return a {@link Function} which returns the {@link ReplyCollection} returned by the given method
     */
    public static Function<? super Method, ReplyCollection> returnReplyCollection(Object obj) {
        return method -> {
            try {
                return (ReplyCollection) method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Could not add Reply Collection", e);
                throw new RuntimeException(e);
            }
        };
    }


    /**
     * Fetches the user who caused the update.
     *
     * @param update a Telegram {@link Update}
     * @return the originating user
     * @throws IllegalStateException if the user could not be found
     */
    public static User getUser(Update update) {
        return defaultIfNull(getUserElseThrow(update), EMPTY_USER);
    }

    private static User getUserElseThrow(Update update) {
        if (Flag.MESSAGE.test(update)) {
            return update.getMessage().getFrom();
        } else if (Flag.CALLBACK_QUERY.test(update)) {
            return update.getCallbackQuery().getFrom();
        } else if (Flag.INLINE_QUERY.test(update)) {
            return update.getInlineQuery().getFrom();
        } else if (Flag.CHANNEL_POST.test(update)) {
            return update.getChannelPost().getFrom();
        } else if (Flag.EDITED_CHANNEL_POST.test(update)) {
            return update.getEditedChannelPost().getFrom();
        } else if (Flag.EDITED_MESSAGE.test(update)) {
            return update.getEditedMessage().getFrom();
        } else if (Flag.CHOSEN_INLINE_QUERY.test(update)) {
            return update.getChosenInlineQuery().getFrom();
        } else if (Flag.SHIPPING_QUERY.test(update)) {
            return update.getShippingQuery().getFrom();
        } else if (Flag.PRECHECKOUT_QUERY.test(update)) {
            return update.getPreCheckoutQuery().getFrom();
        } else if (Flag.POLL_ANSWER.test(update)) {
            return update.getPollAnswer().getUser();
        } else if (Flag.MY_CHAT_MEMBER.test(update)) {
            return update.getMyChatMember().getFrom();
        } else if (Flag.CHAT_MEMBER.test(update)) {
            return update.getChatMember().getFrom();
        } else if (Flag.CHAT_JOIN_REQUEST.test(update)) {
            return update.getChatJoinRequest().getUser();
        } else if (Flag.HAS_BUSINESS_CONNECTION.test(update)) {
            return update.getBusinessConnection().getUser();
        } else if (Flag.HAS_BUSINESS_MESSAGE.test(update)) {
            return update.getBusinessMessage().getFrom();
        } else if (Flag.HAS_EDITED_BUSINESS_MESSAGE.test(update)) {
            return update.getEditedBuinessMessage().getFrom();
        } else if (Flag.HAS_DELETED_BUSINESS_MESSAGE.test(update)) {
            return EMPTY_USER;
        } else if (Flag.POLL.test(update)) {
            return EMPTY_USER;
        } else {
            throw new IllegalStateException("Could not retrieve originating user from update");
        }
    }


}

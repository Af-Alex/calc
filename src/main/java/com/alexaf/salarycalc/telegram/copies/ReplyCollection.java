package com.alexaf.salarycalc.telegram.copies;


import lombok.Getter;

import java.util.Collection;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;

/**
 * A wrapping object containing Replies. Return this in your bot class to get replies registered.
 *
 * @see Reply
 */
@Getter
public class ReplyCollection {

    public final Collection<Reply> replies;

    public ReplyCollection(Collection<Reply> replies) {
        this.replies = replies;
    }

    public static ReplyCollection of(Reply... replies) {
        return new ReplyCollection(newArrayList(replies));
    }

    public Stream<Reply> stream() {
        return replies.stream();
    }
}

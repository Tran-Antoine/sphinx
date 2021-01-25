package net.starpye.quiz.discordimpl.input;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ReactionInputListener implements Consumer<ReactionAddEvent> {

    private Map<TriggerCondition, Consumer<ReactionContext>> callBacks;

    public ReactionInputListener() {
        this.callBacks = new HashMap<>();
    }

    public static Predicate<? super ReactionAddEvent> createFilter() {
        return (event) -> event.getMember().map(member -> !member.isBot()).orElse(false);
    }

    @Override
    public void accept(ReactionAddEvent reactionAddEvent) {

        Snowflake messageId = reactionAddEvent.getMessageId();
        String rawEmoji = reactionAddEvent.getEmoji().asUnicodeEmoji().get().getRaw();

        for (TriggerCondition condition : callBacks.keySet()) {
            if (!condition.messageId.equals(messageId) || !condition.rawReaction.equalsIgnoreCase(rawEmoji)) {
                continue;
            }
            Snowflake userId = reactionAddEvent.getUserId();
            callBacks.get(condition).accept(new ReactionContext(
                    userId,
                    reactionAddEvent.getMember().get().getDisplayName()));
            if (condition.removeReaction) {
                reactionAddEvent
                        .getMessage()
                        .block()
                        .removeReaction(reactionAddEvent.getEmoji(), userId)
                        .block();
            }
        }
    }

    public void addCallBack(TriggerCondition messageId, Consumer<ReactionContext> callBack) {
        callBacks.put(messageId, callBack);
    }

    public void removeCallBack(TriggerCondition messageId) {
        callBacks.remove(messageId);
    }

    public static class TriggerCondition {

        private Snowflake messageId;
        private String rawReaction;
        private boolean removeReaction;

        public TriggerCondition(Snowflake messageId, String rawReaction, boolean removeReaction) {
            this.messageId = messageId;
            this.rawReaction = rawReaction;
            this.removeReaction = removeReaction;
        }

        public Snowflake getMessageId() {
            return messageId;
        }

        public String getRawReaction() {
            return rawReaction;
        }
    }

    public static class ReactionContext {

        private Snowflake userId;
        private String userName;

        public ReactionContext(Snowflake userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public Snowflake getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }
}

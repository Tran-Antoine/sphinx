package net.starpye.quiz.discordimpl.input;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.ReactionAddEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReactionInputListener implements Consumer<ReactionAddEvent> {

    private Map<TriggerCondition, Consumer<ReactionContext>> callBacks;

    public ReactionInputListener() {
        this.callBacks = new HashMap<>();
    }

    @Override
    public void accept(ReactionAddEvent reactionAddEvent) {
        Snowflake messageId = reactionAddEvent.getMessageId();
        String rawEmoji = reactionAddEvent.getEmoji().asUnicodeEmoji().get().getRaw();

        for(TriggerCondition condition : callBacks.keySet()) {
            if(condition.messageId.equals(messageId) && condition.rawReaction.equalsIgnoreCase(rawEmoji)) {
                callBacks.get(condition).accept(new ReactionContext(
                        reactionAddEvent.getUserId()
                ));
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

        public TriggerCondition(Snowflake messageId, String rawReaction) {
            this.messageId = messageId;
            this.rawReaction = rawReaction;
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

        public ReactionContext(Snowflake userId) {
            this.userId = userId;
        }

        public Snowflake getUserId() {
            return userId;
        }
    }
}

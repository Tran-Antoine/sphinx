package net.starype.quiz.discordimpl.input;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageReaction.ReactionEmote;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ReactionInputListener extends ListenerAdapter {

    private Map<TriggerCondition, Consumer<ReactionContext>> callBacks;

    public ReactionInputListener() {
        this.callBacks = new HashMap<>();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent reactionAddEvent) {

        User user = reactionAddEvent.getUser();
        if(user == null || user.isBot()) {
            return;
        }

        String messageId = reactionAddEvent.getMessageId();
        ReactionEmote emote = reactionAddEvent.getReactionEmote();

        if(!emote.isEmoji()) {
            return;
        }

        String rawEmoji = emote.getEmoji();


        for (TriggerCondition condition : callBacks.keySet()) {
            if (!condition.messageId.equals(messageId) || !condition.rawReaction.equalsIgnoreCase(rawEmoji)) {
                continue;
            }
            trigger(reactionAddEvent, condition);
        }
    }


    private void trigger(MessageReactionAddEvent reactionAddEvent, TriggerCondition condition) {

        Member member = reactionAddEvent.getMember();

        if(member == null) {
            return;
        }

        String userId = member.getId();

        callBacks.get(condition).accept(new ReactionContext(
                userId,
                member.getEffectiveName()));

        if (condition.removeReaction) {
            reactionAddEvent
                    .retrieveMessage()
                    .flatMap(message -> message.removeReaction(reactionAddEvent.getReactionEmote().getEmoji(), member.getUser()))
                    .queue();
        }
    }

    public void addCallBack(TriggerCondition messageId, Consumer<ReactionContext> callBack) {
        callBacks.put(messageId, callBack);
    }

    public void removeCallBack(TriggerCondition messageId) {
        callBacks.remove(messageId);
    }

    public static class TriggerCondition {

        private String messageId;
        private String rawReaction;
        private boolean removeReaction;

        public TriggerCondition(String messageId, String rawReaction, boolean removeReaction) {
            this.messageId = messageId;
            this.rawReaction = rawReaction;
            this.removeReaction = removeReaction;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getRawReaction() {
            return rawReaction;
        }
    }

    public static class ReactionContext {

        private String userId;
        private String userName;

        public ReactionContext(String userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public String getUserName() {
            return userName;
        }
    }
}

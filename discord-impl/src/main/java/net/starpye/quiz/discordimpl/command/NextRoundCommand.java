package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import net.starpye.quiz.discordimpl.game.DiscordQuizGame;
import net.starpye.quiz.discordimpl.game.GameList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NextRoundCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        GameList gameList = context.getGameList();
        Snowflake playerId = context.getAuthor().getId();
        TextChannel channel = context.getChannel();
        Message message = context.getMessage();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, playerId);
        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }

        DiscordQuizGame game = gameList.getFromPlayer(playerId).get(); // value guaranteed to be present in our case

        game.addLog(message.getId());
        message.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).subscribe();
        game.addVote(
                playerId,
                () -> onSufficientVotes(channel, game));
    }

    private void onSufficientVotes(TextChannel channel, DiscordQuizGame game) {
        channel.createMessage("All players seem ready. Moving on to the next round!")
                .map(Message::getId)
                .subscribe(game::addLog);
    }

    public static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, Snowflake playerId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> gameList.getFromPlayer(playerId).isEmpty(),
                "You are not in any game");

        conditions.put(
                () -> !gameList.getFromPlayer(playerId).get().isWaitingForNextRound(),
                "You can't vote to begin the next round, since the current one is not finished yet");

        return conditions;
    }

    @Override
    public String getName() {
        return "next";
    }

    @Override
    public String getDescription() {
        return "Mentions that you are ready for the next round";
    }
}

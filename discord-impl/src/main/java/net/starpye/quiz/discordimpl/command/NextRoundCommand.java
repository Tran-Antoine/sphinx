package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import net.starpye.quiz.discordimpl.game.DiscordQuizGame;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starpye.quiz.discordimpl.game.LogContainer;

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

        if(StopConditions.shouldStop(createStopConditions(gameList, playerId), channel)) {
            return;
        }

        DiscordQuizGame game = gameList.getFromPlayer(playerId).get(); // value guaranteed to be present in our case

        game.addLog(message.getId());
        message.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).subscribe();
        game.addVote(
                playerId,
                () -> onSufficientVotes(game, channel));
    }

    private void onSufficientVotes(DiscordQuizGame game, TextChannel channel) {
        game.deleteLogs();
        channel.createMessage("All players seem ready. Moving on to the next round!").subscribe();
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

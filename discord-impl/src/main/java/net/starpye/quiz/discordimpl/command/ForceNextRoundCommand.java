package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
import net.starpye.quiz.discordimpl.game.DiscordQuizGame;
import net.starpye.quiz.discordimpl.game.GameList;

import java.util.Map;
import java.util.function.Supplier;

public class ForceNextRoundCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        GameList gameList = context.getGameList();
        Snowflake playerId = context.getAuthor().getId();
        Message message = context.getMessage();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, playerId);

        if(StopConditions.shouldStop(conditions, context.getChannel(), message)) {
            return;
        }

        DiscordQuizGame game = gameList.getFromPlayer(playerId).get();
        game.addLog(message.getId());
        game.nextRound();
        message.addReaction(ReactionEmoji.unicode("\uD83D\uDC4D")).block();
    }

    public static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, Snowflake authorId) {
        Map<Supplier<Boolean>, String> conditions = NextRoundCommand.createStopConditions(
                gameList,
                authorId
        );
        conditions.put(
                () -> !gameList.getFromPlayer(authorId).get().isAuthor(authorId),
                "Only the game creator can use this command");
        return conditions;
    }

    @Override
    public String getName() {
        return "force-next";
    }

    @Override
    public String getDescription() {
        return "Proceeds to the next round, regardless of whether everyone is ready";
    }
}

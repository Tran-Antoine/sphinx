package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;

import java.util.Map;
import java.util.function.Supplier;

public class ForceNextRoundCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        GameList gameList = context.getGameList();
        String playerId = context.getAuthor().getId();
        Message message = context.getMessage();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, playerId);

        if(StopConditions.shouldStop(conditions, context.getChannel(), message)) {
            return;
        }

        DiscordQuizGame game = gameList.getFromPlayer(playerId).get();
        game.addLog(message.getId());
        game.nextRound();
        message.addReaction("\uD83D\uDC4D").queue();
    }

    public static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = NextRoundCommand.createStopConditions(
                gameList,
                authorId
        );
        conditions.put(
                () -> gameList.getFromPlayer(authorId).isEmpty(),
                "You need to be in a game to use this");
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
        return "Proceed to the next round, regardless of whether everyone is ready";
    }
}

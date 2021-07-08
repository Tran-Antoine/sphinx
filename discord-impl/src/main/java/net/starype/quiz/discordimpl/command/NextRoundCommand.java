package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.starype.quiz.discordimpl.game.DiscordQuizGame;
import net.starype.quiz.discordimpl.game.GameList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NextRoundCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        GameList gameList = context.getGameList();
        String playerId = context.getAuthor().getId();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(gameList, playerId);
        if(StopConditions.shouldStop(conditions, context.getInteraction())) {
            return;
        }

        context.getInteraction()
                .getHook()
                .deleteOriginal()
                .queue();
        DiscordQuizGame game = gameList.getFromPlayer(playerId).get(); // value guaranteed to be present in our case
        game.addVote(playerId, null);
    }

    public static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, String playerId) {
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
        return "Mention that you are ready for the next round";
    }

    @Override
    public CommandData getData() {
        return dataTemplate();
    }
}

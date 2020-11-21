package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import net.starpye.quiz.discordimpl.game.GameList;
import net.starype.quiz.api.game.QuizGame;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SubmitCommand implements QuizCommand {

    @Override
    public String getName() {
        return "submit";
    }

    @Override
    public String getDescription() {
        return "Submits an answer";
    }

    @Override
    public void execute(CommandContext context) {

        Snowflake authorId = context.getAuthor().getId();
        GameList gameList = context.getGameList();
        String[] args = context.getArgs();

        if(StopConditions.shouldStop(createStopConditions(authorId, gameList, args), context.getChannel())) {
            return;
        }

        QuizGame game = gameList.getFromPlayer(authorId).get();
        game.onInputReceived(authorId, args[1].substring(2, args[1].length()-2));
    }

    private Map<Supplier<Boolean>, String> createStopConditions(Snowflake authorId, GameList gameList, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new HashMap<>();
        conditions.put(
                () -> !gameList.isPlaying(authorId),
                "You can't submit an answer if you're not in a game");
        conditions.put(
                () -> args.length != 2 || !args[1].matches("\\|\\|.*?\\|\\|"),
                "Invalid format! Please use \"submit ||your answer||\"");
        return conditions;
    }
}

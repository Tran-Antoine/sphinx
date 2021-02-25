package net.starype.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Message;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.api.game.QuizGame;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class SubmitCommand implements QuizCommand {

    @Override
    public String getName() {
        return "submit";
    }

    @Override
    public String getDescription() {
        return "Submit an answer. The format for answer submission is: `?create ||your answer||`";
    }

    @Override
    public void execute(CommandContext context) {

        Snowflake authorId = context.getAuthor().getId();
        GameList gameList = context.getGameList();
        String[] args = context.getArgs();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(authorId, gameList, args);
        Message message = context.getMessage();

        if(StopConditions.shouldStop(conditions, context.getChannel(), message)) {
            return;
        }

        QuizGame game = gameList.getFromPlayer(authorId).get();
        game.sendInput(authorId, args[1].substring(2, args[1].length()-2));
        message.delete().block();
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(Snowflake authorId, GameList gameList, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> !gameList.isPlaying(authorId),
                "You can't submit an answer if you're not in a game");
        conditions.put(
                () -> args.length != 2 || !args[1].matches("\\|\\|.*?\\|\\|"),
                "Invalid format! Please use \"submit ||your answer||\"");
        return conditions;
    }
}

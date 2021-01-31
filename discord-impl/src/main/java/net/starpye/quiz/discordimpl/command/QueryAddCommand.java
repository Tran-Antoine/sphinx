package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.GameLobby;
import net.starpye.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.api.database.QuestionQueries;
import net.starype.quiz.api.database.QuestionQuery;
import net.starype.quiz.api.game.question.QuestionDifficulty;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class QueryAddCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        LobbyList lobbyList = context.getLobbyList();
        Snowflake authorId = context.getAuthor().getId();
        String[] args = context.getArgs();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId, args);
        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        BiConsumer<GameLobby, QuestionQuery> queryAction = findQueryAction(args[1]);

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        Function<String, QuestionQuery> queryType = findQueryType(args[2]);

        queryAction.accept(lobby, queryType.apply(args[3])); // both guaranteed non-null
        channel.createMessage("Successfully added query").subscribe();
    }

    private static BiConsumer<GameLobby, QuestionQuery> findQueryAction(String arg) {
        switch (arg) {
            case "and-query": return GameLobby::andQuery;
            case "or-query": return GameLobby::orQuery;
            default: return null;
        }
    }

    private static Function<String, QuestionQuery> findQueryType(String arg) {
        switch (arg) {
            case "directory": return QuestionQueries::allFromDirectory;
            case "tag": return QuestionQueries::allWithTag;
            case "difficulty": return diff -> QuestionQueries.allWithDifficulty(QuestionDifficulty.valueOf(diff));
            default: return null;
        }
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, Snowflake authorId, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        String syntax = "Syntax: ?add-query [and-query|or-query] [directory|tag|difficulty] <value>";
        conditions.put(
                () -> args.length != 4 || findQueryAction(args[1]) == null || findQueryType(args[2]) == null,
                syntax);

        conditions.put(() -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You must be the creator of the lobby to use this");
        return conditions;
    }

    @Override
    public String getName() {
        return "add-query";
    }

    @Override
    public String getDescription() {
        return null;
    }
}

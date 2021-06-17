package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.api.database.QuestionQueries;
import net.starype.quiz.api.database.QuestionQuery;
import net.starype.quiz.api.question.QuestionDifficulty;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class QueryAddCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        LobbyList lobbyList = context.getLobbyList();
        String authorId = context.getAuthor().getId();
        MessageChannel channel = context.getChannel();
        CommandInteraction interaction = context.getInteraction();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId);
        if (StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        BiConsumer<GameLobby, QuestionQuery> queryAction = findQueryAction(interaction);

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        Function<String, QuestionQuery> queryType = findQueryType(interaction);

        queryAction.accept(lobby, queryType.apply(interaction.getOption("query-value").getAsString())); // both guaranteed non-null
        channel.sendMessage("Successfully added query").queue(null, null);
    }

    private static BiConsumer<GameLobby, QuestionQuery> findQueryAction(CommandInteraction interaction) {

        OptionMapping operator = interaction.getOption("query-operator");
        if(operator == null) {
            return GameLobby::andQuery;
        }

        switch (operator.getAsString()) {
            case "and":
                return GameLobby::andQuery;
            case "or":
                return GameLobby::orQuery;
            default:
                return null;
        }
    }

    private static Function<String, QuestionQuery> findQueryType(CommandInteraction interaction) {
        switch (interaction.getOption("query-type").getAsString()) {
            case "directory":
                return QuestionQueries::allFromDirectory;
            case "tag":
                return QuestionQueries::allWithTag;
            case "difficulty":
                return diff -> QuestionQueries.allWithDifficulty(QuestionDifficulty.valueOf(diff));
            default:
                return null;
        }
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
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
        return "Add a query filtering the questions. Syntax: ?add-query [and-query|or-query] [directory|tag|difficulty] <value>";
    }

    @Override
    public CommandData getData() {
        return dataTemplate()
                .addOptions(
                        new OptionData(OptionType.STRING, "query-operator", "query chaining operator (and / or)", false)
                                .addChoices(
                                        new Command.Choice("and", "and"),
                                        new Command.Choice("or", "or")),
                        new OptionData(OptionType.STRING, "query-type", "query type", true)
                                .addChoices(
                                        new Command.Choice("directory", "directory"),
                                        new Command.Choice("tag", "tag"),
                                        new Command.Choice("difficulty", "difficulty")),
                        new OptionData(OptionType.STRING, "query-value", "value of the query, i.e HARD", true)
                );
    }
}

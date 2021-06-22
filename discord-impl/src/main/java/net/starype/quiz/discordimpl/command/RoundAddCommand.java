package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.parser.ConfigMapper;
import net.starype.quiz.api.parser.ConfigMatcher;
import net.starype.quiz.api.question.Question;
import net.starype.quiz.api.round.*;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class RoundAddCommand implements QuizCommand {

    private static final int MAX_ROUNDS_AT_ONCE = 20;

    private static final ConfigMapper<PartialRound> DEFAULT =
            new RoundMapper((q) -> new IndividualRoundFactory().create(q, 1), "individual");

    private static final ConfigMatcher<PartialRound> ROUND_MATCHER = new ConfigMatcher<>(Arrays.asList(
            DEFAULT,
            new RoundMapper((q) -> new RaceRoundFactory().create(q, 1, 1.5), "race"),
            new RoundMapper((q) -> new ClassicalRoundFactory().create(q, 3, 1), "classical"),
            new RoundMapper((q) -> new PollRoundFactory().create(q, 1), "poll")
    ), DEFAULT);

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        String authorId = context.getAuthor().getId();
        CommandInteraction interaction = context.getInteraction();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId);
        if(StopConditions.shouldStop(conditions, interaction)) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        String roundName = Optional.ofNullable(interaction.getOption("round-type"))
                .map(OptionMapping::getAsString)
                .orElse("individual");

        PartialRound matchedRound = ROUND_MATCHER.loadFromValueOrDefault(roundName, null);

        int count = Optional.ofNullable(interaction.getOption("count"))
                .map(OptionMapping::getAsLong)
                .map(x -> Math.min(20, x))
                .orElse(1L)
                .intValue();

        for(int i = 0; i < count; i++) {
            lobby.queueRound(matchedRound);
        }

        MessageUtils.sendAndTrack(
                "Round successfully added (if no known round type matches the query, 'individual' is picked instead)",
                interaction,
                lobby
        );
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You must be the creator of the lobby to use this");

        return conditions;
    }

    @Override
    public String getName() {
        return "add-round";
    }

    @Override
    public String getDescription() {
        return "Add a round to the list. Syntax: ?add-round <race|classical|individual> [count]";
    }

    private static class RoundMapper implements ConfigMapper<PartialRound> {

        private final PartialRound supplier;
        private final String name;

        RoundMapper(PartialRound supplier, String name) {
            this.supplier = supplier;
            this.name = name;
        }

        @Override
        public String getMapperName() {
            return name;
        }

        @Override
        public PartialRound map(ReadableRawMap config) {
            return supplier;
        }
    }

    public interface PartialRound extends Function<Question, QuizRound> { }

    @Override
    public CommandData getData() {
        return dataTemplate()
                .addOptions(
                        new OptionData(OptionType.STRING, "round-type", "type of round to queue", false).addChoices(
                                new Command.Choice("race", "race"),
                                new Command.Choice("classical", "classical"),
                                new Command.Choice("individual", "individual")),
                        new OptionData(OptionType.INTEGER, "count", "how many rounds of the type to add", false)
                );
    }
}

package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.TextChannel;
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
        String[] args = context.getArgs();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId, args);
        if(StopConditions.shouldStop(
                conditions,
                channel,
                context.getMessage())) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        PartialRound matchedRound = ROUND_MATCHER.loadFromValueOrDefault(args[1], null);
        int count = args.length <= 2
                ? 1
                : asRoundCount(args[2]).get();

        for(int i = 0; i < count; i++) {
            lobby.queueRound(matchedRound);
        }

        lobby.trackMessage(context.getMessage().getId());
        MessageUtils.sendAndTrack(
                "Round successfully added (if no known round type matches the query, 'individual' is picked instead)",
                channel,
                lobby
        );
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, String authorId, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You must be the creator of the lobby to use this");

        conditions.put(
                () -> args.length < 2,
                "You must specify the type of round you wish to queue (either race, classical or individual)");

        conditions.put(
                () -> args.length >= 3 && asRoundCount(args[2]).isEmpty(),
                "Second argument must be a number between 1 and 20");

        return conditions;
    }

    private static Optional<Integer> asRoundCount(String arg) {
        try {
            int value = Integer.parseInt(arg);
            if(value > 0 && value <= MAX_ROUNDS_AT_ONCE) {
                return Optional.of(value);
            }
            return Optional.empty();
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
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

        private PartialRound supplier;
        private String name;

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
}

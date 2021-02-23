package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.GameLobby;
import net.starpye.quiz.discordimpl.game.LobbyList;
import net.starpye.quiz.discordimpl.util.MessageUtils;
import net.starype.quiz.api.database.ReadableRawMap;
import net.starype.quiz.api.game.*;
import net.starype.quiz.api.game.question.Question;
import net.starype.quiz.api.parser.ConfigMapper;
import net.starype.quiz.api.parser.ConfigMatcher;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RoundAddCommand implements QuizCommand {

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
        Snowflake authorId = context.getAuthor().getId();
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
        PartialRound matchedRound = ROUND_MATCHER.loadFromValue(args[1], null).get();
        int count = args.length <= 2
                ? 1
                : Integer.parseInt(args[2]);

        for(int i = 0; i < count; i++) {
            lobby.queueRound(matchedRound);
        }

        lobby.trackMessage(context.getMessage().getId());
        MessageUtils.sendAndTrack(
                "Round successfully added",
                channel,
                lobby
        );
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, Snowflake authorId, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You must be the creator of the lobby to use this");

        conditions.put(
                () -> args.length < 2,
                "You must specify the type of round you wish to queue");

        conditions.put(
                () -> ROUND_MATCHER.loadFromValue(args[1], null).isEmpty(),
                "Could not find any round type matching the given argument");

        conditions.put(
                () -> args.length == 3 && (args[2].length() != 1 || !Character.isDigit(args[2].charAt(0))),
                "Second argument must be a number between 0 and 9");

        return conditions;
    }

    @Override
    public String getName() {
        return "add-round";
    }

    @Override
    public String getDescription() {
        return "Add a round to the list";
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

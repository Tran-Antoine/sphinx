package net.starype.quiz.discordimpl.command.event;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.database.*;
import net.starype.quiz.api.question.QuestionDifficulty;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.discordimpl.command.CommandContext;
import net.starype.quiz.discordimpl.command.QuizCommand;
import net.starype.quiz.discordimpl.command.StopConditions;
import net.starype.quiz.discordimpl.game.FlexiblePlayersRoundFactory;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.InputUtils;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WeeklyQuizCommand implements QuizCommand {

    private static final String PATH = "discord-impl/src/main/resources/event/Week";
    private static final LocalDate START_TIME = LocalDate.of(2022, Month.FEBRUARY, 21);

    @Override
    public String getName() {
        return "weekly";
    }

    @Override
    public String getDescription() {
        return "Weekly quizzes on CS108 content";
    }

    @Override
    public void execute(CommandContext context) {

        GameList gameList = context.getGameList();
        LobbyList lobbyList = context.getLobbyList();
        Member author = context.getAuthor();
        TextChannel channel = context.getChannel();
        Message message = context.getMessage();
        String[] args = context.getArgs();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(
                gameList, lobbyList,
                author, author.getEffectiveName(),
                args);

        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }

        int week = Integer.parseInt(args[1]);
        Queue<QuizRound> questions = findQuestions(week, channel);

        if(questions.isEmpty()) {
            return;
        }

        gameList.startNewGame(
                new ArrayList<>(Collections.singleton(author.getId())),
                questions, channel,
                author.getId(),
                () -> {},
                channel.getGuild().getId(),
                false);
    }

    private Queue<QuizRound> findQuestions(int week, TextChannel channel) {

        if(!isAvailable(week, channel)) {
            //return new LinkedList<>();
        }

        String path = PATH + week + ".zip";
        Collection<? extends EntryUpdater> entries = InputUtils.loadEntryUpdatersFromLocalPath(path, channel);
        SerializedIO serializedIO = new ByteSerializedIO(new byte[0], new AtomicReference<>());
        QuestionDatabase db = new QuestionDatabase(entries, serializedIO, false);
        try {
            db.sync();
        } catch (Exception ignored) {
            return new LinkedList<>();
        }

        return db.listQuery(QuestionQueries.ALL)
                .stream()
                .sorted(Comparator.comparingInt(a -> a.getDifficulty().ordinal()))
                .map(q -> new FlexiblePlayersRoundFactory().create(q, mapDifficulty(q.getDifficulty()), channel))
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private boolean isAvailable(int week, TextChannel channel) {
        LocalDate now = LocalDate.now();
        LocalDate required = START_TIME.plusWeeks(week - 1);
        if(now.isAfter(required)) {
            return true;
        }
        String format = required
                .format(DateTimeFormatter.ofPattern("MMMM d", Locale.ENGLISH));
        channel.sendMessage("Access denied <:pandadiablotin:843123536825548810>\n" +
                "This quiz will be available on `" + format + "` . Stay tuned!").queue(null, null);
        return false;
    }

    private static double mapDifficulty(QuestionDifficulty diff) {
        switch (diff) {
            case EASY:
            case NORMAL:
                return 1;
            case HARD:
            case INSANE:
            default:
                return 1.5;
        }
    }

    private Map<Supplier<Boolean>, String> createStopConditions(
            GameList gameList, LobbyList lobbyList, Member author, String nickName, String[] args) {

        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> args.length != 2 || !args[1].matches("\\b([1-9]|1[0-4])\\b"),
                "Invalid syntax: please use ?weekly <week number from 1 to 14>");

        conditions.put(
                () -> lobbyList.findByPlayer(author.getId()).isPresent(),
                nickName + ", you are already in a lobby");

        conditions.put(
                () -> gameList.isPlaying(author),
                nickName + ", you are already playing a game");

        return conditions;
    }
}

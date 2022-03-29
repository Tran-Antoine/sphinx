package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.discordimpl.game.GameList;

import java.util.Arrays;
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
        return "Submit an answer. The format for answer submission is: `?submit ||your answer||`";
    }

    @Override
    public void execute(CommandContext context) {

        Member author = context.getAuthor();
        GameList gameList = context.getGameList();
        String[] args = reconstructArgs(context.getArgs());
        TextChannel channel = context.getChannel();
        Map<Supplier<Boolean>, String> conditions = createStopConditions(author, channel, gameList, args);
        Message message = context.getMessage();

        if(StopConditions.shouldStop(conditions, channel, message)) {
            return;
        }

        QuizGame game = gameList.findGameFor(author, channel, true).get();
        game.sendInput(author.getId(), args[1].substring(2, args[1].length()-2));
        message.delete().queue(null, null);
    }

    private static String[] reconstructArgs(String[] args) {
        if(args.length < 2) {
            return args;
        }

        String[] newArgs = new String[2];
        newArgs[0] = args[0];
        newArgs[1] = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        return newArgs;
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(Member author, TextChannel channel, GameList gameList, String[] args) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> !gameList.canPlay(author, channel),
                "You can't submit an answer if you're not in a game");
        conditions.put(
                () -> args.length != 2 || !args[1].matches("\\|\\|.*?\\|\\|"),
                "Invalid format! Please use \"submit ||your answer||\"");
        return conditions;
    }
}

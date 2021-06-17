package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.commands.CommandInteraction;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
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

        String authorId = context.getAuthor().getId();
        GameList gameList = context.getGameList();
        CommandInteraction interaction = context.getInteraction();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(authorId, gameList);

        if(StopConditions.shouldStop(conditions, context.getChannel())) {
            return;
        }

        QuizGame game = gameList.getFromPlayer(authorId).get();
        game.sendInput(authorId, interaction.getOption("answer").getAsString());
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(String authorId, GameList gameList) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> !gameList.isPlaying(authorId),
                "You can't submit an answer if you're not in a game");
        return conditions;
    }

    @Override
    public CommandData getData() {
        return dataTemplate().addOptions(
                new OptionData(OptionType.STRING, "answer", "your answer to the question", true)
        );
    }
}

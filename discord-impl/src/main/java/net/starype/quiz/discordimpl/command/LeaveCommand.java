package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.game.GameList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LeaveCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        String authorId = context.getAuthor().getId();
        GameList gameList = context.getGameList();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(gameList, authorId);

        if(StopConditions.shouldStop(stopConditions, channel, context.getMessage())) {
            return;
        }
        gameList.getFromPlayer(authorId).get().removePlayer(authorId);
        channel.sendMessage("Successfully left the game").queue();
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = new HashMap<>();
        conditions.put(
                () -> !gameList.isPlaying(authorId),
                "You are not registered in any game");
        return conditions;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the current game. No coming back!";
    }
}

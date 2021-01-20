package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.GameList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LeaveCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        Snowflake authorId = context.getAuthor().getId();
        GameList gameList = context.getGameList();
        TextChannel channel = context.getChannel();
        if(StopConditions.shouldStop(createStopConditions(gameList, authorId), channel)) {
            return;
        }
        gameList.getFromPlayer(authorId).get().removePlayer(authorId);
        channel.createMessage("Successfully left the game").block();
    }

    private Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, Snowflake authorId) {
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
        return "Leaves the current game. No coming back!";
    }
}

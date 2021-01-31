package net.starpye.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.game.LobbyList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClearQueryCommand implements QuizCommand {
    @Override
    public void execute(CommandContext context) {

        LobbyList lobbyList = context.getLobbyList();
        Snowflake authorId = context.getAuthor().getId();
        String[] args = context.getArgs();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId);
        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        lobbyList
                .findByAuthor(authorId)
                .get()
                .resetQuery();

        channel.createMessage("Successfully reset the current query").subscribe();
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, Snowflake authorId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(() -> lobbyList.findByAuthor(authorId).isEmpty(),
                "You must be the creator of the lobby to use this");
        return conditions;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}

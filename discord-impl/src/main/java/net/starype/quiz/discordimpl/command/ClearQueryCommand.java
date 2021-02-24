package net.starype.quiz.discordimpl.command;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.MessageUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ClearQueryCommand implements QuizCommand {
    @Override
    public void execute(CommandContext context) {

        LobbyList lobbyList = context.getLobbyList();
        Snowflake authorId = context.getAuthor().getId();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId);
        if(StopConditions.shouldStop(conditions, channel, context.getMessage())) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        lobby.resetQuery();

        lobby.trackMessage(context.getMessage().getId());
        MessageUtils.sendAndTrack(
                "Successfully reset the current query",
                channel,
                lobby);
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
        return "clear-query";
    }

    @Override
    public String getDescription() {
        return "Clear all the previous queries added";
    }
}

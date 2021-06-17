package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
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
        String authorId = context.getAuthor().getId();
        MessageChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId);
        if(StopConditions.shouldStop(conditions, channel)) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        lobby.resetQuery();

        MessageUtils.sendAndTrack(
                "Successfully reset the current query",
                channel,
                lobby);
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(
            LobbyList lobbyList, String authorId) {
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
        return "Clear all the previous queries";
    }

    @Override
    public CommandData getData() {
        return dataTemplate();
    }
}

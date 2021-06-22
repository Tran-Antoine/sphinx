package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.LobbyList;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class LeaveCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {
        Member author = context.getAuthor();
        String authorId = author.getId();
        GameList gameList = context.getGameList();
        LobbyList lobbyList = context.getLobbyList();
        MessageChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(gameList, lobbyList, authorId);

        if(StopConditions.shouldStop(stopConditions, context.getInteraction())) {
            return;
        }
        gameList.getFromPlayer(authorId).ifPresent(game -> game.removePlayer(authorId));
        lobbyList.findByPlayer(authorId).ifPresent(lobby -> lobby.unregisterPlayer(authorId, author.getEffectiveName()));
        channel.sendMessage("Successfully left the game/lobby").queue(null, null);
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(GameList gameList, LobbyList lobbyList, String authorId) {
        Map<Supplier<Boolean>, String> conditions = new HashMap<>();
        conditions.put(
                () -> !gameList.isPlaying(authorId) && lobbyList.findByPlayer(authorId).isEmpty(),
                "You are not registered in any game or lobby");
        return conditions;
    }

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "Leave the current game or lobby. If it's a game, there is no coming back!";
    }

    @Override
    public CommandData getData() {
        return dataTemplate();
    }
}

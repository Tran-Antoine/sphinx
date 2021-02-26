package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class StartGameCommand implements QuizCommand {

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Start a game. Must follow /create";
    }

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        Member author = context.getAuthor();
        String authorId = author.getId();

        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId, author.getEffectiveName());

        if(StopConditions.shouldStop(conditions, context.getChannel(), context.getMessage())) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        lobby.trackMessage(context.getMessage().getId());
        if(lobby.start(context.getGameList())) {
            lobbyList.unregisterLobby(lobby);
        }
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, String playerId,
                                                                       String nickName) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();

        conditions.put(
                () -> lobbyList.findByPlayer(playerId).isEmpty(),
                nickName + ", you are not in any lobby");
        conditions.put(
                () -> lobbyList.findByAuthor(playerId).isEmpty(),
                nickName + ", only the owner of the lobby can start the game");

        return conditions;
    }
}

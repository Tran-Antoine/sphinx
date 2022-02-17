package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.CounterLimiter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class StartCommand implements QuizCommand {

    private final static CounterLimiter gameLimiter = new CounterLimiter(5);

    protected abstract void onPreStart(GameLobby lobby);

    @Override
    public void execute(CommandContext context) {
        LobbyList lobbyList = context.getLobbyList();
        Member author = context.getAuthor();
        String authorId = author.getId();
        Message message = context.getMessage();

        long uniqueId = lobbyList
                .findByAuthor(authorId)
                .map(name -> name.getName().hashCode())
                .orElse(0);
        Map<Supplier<Boolean>, String> conditions = createStopConditions(lobbyList, authorId, author.getEffectiveName(), uniqueId);

        if(StopConditions.shouldStop(conditions, context.getChannel(), message)) {
            return;
        }

        GameLobby lobby = lobbyList.findByAuthor(authorId).get();
        lobby.trackMessage(message.getId());

        onPreStart(lobby);

        if(lobby.start(context.getGameList(), () -> gameLimiter.unregister(uniqueId))) {
            lobbyList.unregisterLobby(lobby);
        }
    }

    private static Map<Supplier<Boolean>, String> createStopConditions(LobbyList lobbyList, String playerId,
                                                                       String nickName, long uniqueId) {
        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();

        conditions.put(
                () -> lobbyList.findByPlayer(playerId).isEmpty(),
                nickName + ", you are not in any lobby");

        conditions.put(
                () -> lobbyList.findByAuthor(playerId).isEmpty(),
                nickName + ", only the owner of the lobby can start the game");

        conditions.put(
                () -> !gameLimiter.register(uniqueId),
                "Error: Cannot create a new game as the maximum number of games has been reached");


        return conditions;
    }
}
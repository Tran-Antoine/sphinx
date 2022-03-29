package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;
import net.starype.quiz.discordimpl.util.CounterLimiter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CreateLobbyCommand implements QuizCommand {

    private static final CounterLimiter lobbyLimiter = new CounterLimiter(5);

    @Override
    public void execute(CommandContext context) {

        Member author = context.getAuthor();
        String playerId = author.getId();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(
                context.getGameList(),
                context.getLobbyList(),
                author,
                author.getEffectiveName());

        Message message = context.getMessage();

        if(StopConditions.shouldStop(stopConditions, channel, message)) {
            lobbyLimiter.unregisterIfPresent(context.getAuthor().getIdLong());
            return;
        }

        LobbyList lobbies = context.getLobbyList();
        GameLobby lobby = lobbies.registerLobby(channel, author, () -> lobbyLimiter.unregister(playerId.hashCode()));
        lobby.trackMessage(message.getId());
    }

    private Map<Supplier<Boolean>, String> createStopConditions(
            GameList gameList, LobbyList lobbyList, Member author, String nickName) {

        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByPlayer(author.getId()).isPresent(),
                nickName + ", you are already in a lobby");

        conditions.put(
                () -> gameList.isPlaying(author),
                nickName + ", you are already playing a game");

        conditions.put(
               () -> !lobbyLimiter.register(author.hashCode()),
               "Error: Cannot create a new lobby as the maximum number of lobbies has been reached");


        return conditions;
    }

    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Set up a game lobby that can be started at the author's request";
    }
}

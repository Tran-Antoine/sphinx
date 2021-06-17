package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
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
        MessageChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(
                context.getGameList(),
                context.getLobbyList(),
                playerId,
                author.getEffectiveName());

        if(StopConditions.shouldStop(stopConditions, channel)) {
            lobbyLimiter.unregisterIfPresent(context.getAuthor().getIdLong());
            return;
        }

        LobbyList lobbies = context.getLobbyList();
        lobbies.registerLobby(channel, author, () -> lobbyLimiter.unregister(playerId.hashCode()), context.getInteraction().getGuild().getId());
    }

    private Map<Supplier<Boolean>, String> createStopConditions(
            GameList gameList, LobbyList lobbyList, String authorId, String nickName) {

        Map<Supplier<Boolean>, String> conditions = new LinkedHashMap<>();
        conditions.put(
                () -> lobbyList.findByPlayer(authorId).isPresent(),
                nickName + ", you are already in a lobby");

        conditions.put(
                () -> gameList.isPlaying(authorId),
                nickName + ", you are already playing a game");

        conditions.put(
               () -> !lobbyLimiter.register(authorId.hashCode()),
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

    @Override
    public CommandData getData() {
        return dataTemplate();
    }
}

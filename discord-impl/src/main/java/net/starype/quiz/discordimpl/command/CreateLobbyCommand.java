package net.starype.quiz.discordimpl.command;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.game.GameList;
import net.starype.quiz.discordimpl.game.GameLobby;
import net.starype.quiz.discordimpl.game.LobbyList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CreateLobbyCommand implements QuizCommand {

    @Override
    public void execute(CommandContext context) {

        if(!context.getDiscordContext().lobbyLimiter().acquireInstance(Thread.currentThread().getId())) {
            context.getChannel().sendMessage("Error: max lobby limit has been reached").queue();
            return;
        }

        Member author = context.getAuthor();
        String playerId = author.getId();
        TextChannel channel = context.getChannel();

        Map<Supplier<Boolean>, String> stopConditions = createStopConditions(
                context.getGameList(),
                context.getLobbyList(),
                playerId,
                author.getEffectiveName());

        Message message = context.getMessage();

        if(StopConditions.shouldStop(stopConditions, channel, message)) {
            return;
        }

        LobbyList lobbies = context.getLobbyList();
        GameLobby lobby = lobbies.registerLobby(channel, author,
                () -> context.getDiscordContext().lobbyLimiter().releaseInstance(Thread.currentThread().getId()));
        lobby.trackMessage(message.getId());
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

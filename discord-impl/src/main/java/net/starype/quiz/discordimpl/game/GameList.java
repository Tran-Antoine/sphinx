package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.server.ServerGate;
import net.starype.quiz.discordimpl.user.DiscordPlayer;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameList {

    private Map<DiscordQuizGame, ScheduledFuture<?>> ongoingGames;

    public GameList() {
        this.ongoingGames = new HashMap<>();
    }

    public void startNewGame(Collection<? extends String> playersId, Queue<? extends QuizRound> rounds, TextChannel channel, String authorId) {
        Collection<DiscordPlayer> gamePlayers = asGamePlayers(playersId, channel);
        DiscordGameServer server = new DiscordGameServer(channel, this::stopGame);
        ServerGate<DiscordQuizGame> gate = server.createGate();
        DiscordQuizGame game = new DiscordQuizGame(rounds, gamePlayers, gate, authorId, server);
        ScheduledExecutorService autoCancel = Executors.newScheduledThreadPool(1);
        autoCancel.schedule(() -> stopGame(game, true, channel), 60, TimeUnit.MINUTES);

        ScheduledExecutorService autoUpdate = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = autoUpdate.scheduleAtFixedRate(game::update, 0, 250, TimeUnit.MILLISECONDS);
        game.start();
        ongoingGames.put(game, future);
    }

    private Collection<DiscordPlayer> asGamePlayers(Collection<? extends String> playersId, TextChannel channel) {
        Guild guild = channel.getGuild();
        return playersId
                .stream()
                .map(id -> guild.retrieveMemberById(id).complete())
                .map(this::asPlayer)
                .collect(Collectors.toList());
    }

    public boolean isPlaying(String playerId) {
        return ongoingGames
                .keySet()
                .stream()
                .anyMatch((game) -> game.containsPlayerId(playerId));
    }

    public Optional<DiscordQuizGame> getFromPlayer(String playerId) {
        return ongoingGames
                .keySet()
                .stream()
                .filter(game -> game.containsPlayerId(playerId))
                .findAny();
    }

    public void stopGame(DiscordQuizGame game) {
        stopGame(game, false, null);
    }

    public void stopGame(DiscordQuizGame game, boolean forced, TextChannel channel) {
        ScheduledFuture<?> future = ongoingGames.remove(game);
        if(future == null) {
            return;
        }
        future.cancel(true);
        if(forced) {
            game.deleteLogs();
            channel.sendMessage("Game lasted too long, I had to stop it <:pandasad:805105368505384970>").queue(null, null);
        }
    }

    private DiscordPlayer asPlayer(Member member) {
        String userName = member.getUser().getName();
        String nickName = member.getEffectiveName();
        String id = member.getId();
        return new DiscordPlayer(id, userName, nickName);
    }
}

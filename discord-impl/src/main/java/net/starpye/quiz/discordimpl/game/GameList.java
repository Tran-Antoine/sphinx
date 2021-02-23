package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.user.DiscordPlayer;
import net.starype.quiz.api.game.QuizRound;
import net.starype.quiz.api.server.ServerGate;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class GameList {

    private Map<DiscordQuizGame, ScheduledFuture<?>> ongoingGames;

    public GameList() {
        this.ongoingGames = new HashMap<>();
    }

    public void startNewGame(Collection<? extends Snowflake> playersId, Queue<? extends QuizRound> rounds, TextChannel channel, Snowflake authorId) {
        Collection<DiscordPlayer> gamePlayers = asGamePlayers(playersId, channel);
        DiscordGameServer server = new DiscordGameServer(channel, this::stopGame);
        ServerGate<DiscordQuizGame> gate = server.createGate();
        DiscordQuizGame game = new DiscordQuizGame(rounds, gamePlayers, gate, authorId, server);
        ScheduledExecutorService task = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = task.scheduleAtFixedRate(game::update, 0, 250, TimeUnit.MILLISECONDS);
        game.start();
        ongoingGames.put(game, future);
    }

    private Collection<DiscordPlayer> asGamePlayers(Collection<? extends Snowflake> playersId, TextChannel channel) {
        return Flux.fromIterable(playersId)
                .flatMap(id -> channel.getGuild().flatMap(guild -> guild.getMemberById(id)))
                .map(this::asPlayer)
                .collectList()
                .block();
    }

    public boolean isPlaying(Snowflake playerId) {
        return ongoingGames
                .keySet()
                .stream()
                .anyMatch((game) -> game.containsPlayerId(playerId));
    }

    public Optional<DiscordQuizGame> getFromPlayer(Snowflake playerId) {
        return ongoingGames
                .keySet()
                .stream()
                .filter(game -> game.containsPlayerId(playerId))
                .findAny();
    }

    public void stopGame(DiscordQuizGame game) {
        ScheduledFuture<?> future = ongoingGames.remove(game);
        if(future == null) {
            return;
        }
        future.cancel(true);
    }

    private DiscordPlayer asPlayer(Member member) {
        String userName = member.getUsername();
        String nickName = member.getDisplayName();
        Snowflake id = member.getId();
        return new DiscordPlayer(id, userName, nickName);
    }
}

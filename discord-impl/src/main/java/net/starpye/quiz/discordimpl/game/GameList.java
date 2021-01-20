package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.user.DiscordPlayer;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.server.GameServer;
import net.starype.quiz.api.server.ServerGate;

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

    public void startNewGame(Collection<? extends Snowflake> playersId, Queue<? extends GameRound> rounds, TextChannel channel, Snowflake authorId) {
        Set<DiscordPlayer> gamePlayers = playersId
                .stream()
                .map(id -> asPlayer(channel.getGuild().block(), id))
                .collect(Collectors.toSet());
        GameServer<DiscordQuizGame> server = new DiscordGameServer(channel, this::stopGame);
        ServerGate<DiscordQuizGame> gate = server.createGate();
        DiscordQuizGame game = new DiscordQuizGame(rounds, gamePlayers, gate, authorId);
        ScheduledExecutorService task = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = task.scheduleAtFixedRate(game::update, 0, 250, TimeUnit.MILLISECONDS);
        game.start();
        ongoingGames.put(game, future);
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

    private DiscordPlayer asPlayer(Guild guild, Snowflake id) {
        Member player = guild.getMemberById(id).block();
        String userName = player.getUsername();
        String nickName = player.getDisplayName();
        return new DiscordPlayer(id, userName, nickName);
    }
}

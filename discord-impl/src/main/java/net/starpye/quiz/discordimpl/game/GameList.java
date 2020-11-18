package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import net.starpye.quiz.discordimpl.user.DiscordPlayer;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.QuizGame;
import net.starype.quiz.api.game.SimpleGame;
import net.starype.quiz.api.server.GameServer;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GameList {

    private Map<QuizGame, ScheduledFuture<?>> ongoingGames;

    public GameList() {
        this.ongoingGames = new HashMap<>();
    }

    public void startNewGame(Collection<? extends Snowflake> playersId, Queue<? extends GameRound> rounds, Guild guild) {
        Set<DiscordPlayer> gamePlayers = playersId
                .stream()
                .map(id -> asPlayer(guild, id))
                .collect(Collectors.toSet());
        GameServer server = new DiscordGameServer();
        QuizGame game = new SimpleGame(rounds, gamePlayers, server);
        ScheduledExecutorService task = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> future = task.scheduleAtFixedRate(game::update, 0, 250, TimeUnit.MILLISECONDS);

        ongoingGames.put(game, future);
    }

    public boolean isPlaying(Snowflake playerId) {
        return ongoingGames
                .keySet()
                .stream()
                .anyMatch((game) -> game.containsPlayerId(playerId));
    }

    public Optional<QuizGame> getFromPlayer(Snowflake playerId) {
        return ongoingGames
                .keySet()
                .stream()
                .filter(game -> game.containsPlayerId(playerId))
                .findAny();
    }

    public void stopGame(QuizGame game) {
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

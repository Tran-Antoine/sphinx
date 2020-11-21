package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import net.starype.quiz.api.game.GameRound;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class GameLobby {

    private String name;
    private TextChannel channel;
    private Set<Snowflake> playersId;
    private Snowflake authorId;
    private Queue<GameRound> rounds;

    public GameLobby(TextChannel channel, String name) {
        this.channel = channel;
        this.name = name;
        this.playersId = new HashSet<>();
        this.rounds = GameRounds.DEFAULT_PRESET;
    }

    public void registerAuthor(Snowflake playerId) {
        this.authorId = playerId;
        registerPlayer(playerId);
    }

    public void registerPlayer(Snowflake playerId) {
        playersId.add(playerId);
    }

    public void unregisterPlayer(Snowflake playerID) {
        playersId.remove(playerID);
    }

    public void queueRound(GameRound round) {
        rounds.add(round);
    }

    public void unqueueRound(GameRound round) {
        rounds.remove(round);
    }

    public boolean containsPlayer(Snowflake authorId) {
        return playersId.contains(authorId);
    }

    public void start(GameList gameList) {
        gameList.startNewGame(playersId, rounds, channel, authorId);
    }

    public boolean isAuthor(Snowflake playerId) {
        return authorId != null && authorId.equals(playerId);
    }

    public boolean isName(String name) {
        return this.name.equals(name);
    }
}

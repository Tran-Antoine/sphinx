package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import net.starype.quiz.api.game.GameRound;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class GameLobby {

    private Guild guild;
    private Set<Snowflake> playersId;
    private Snowflake authorId;
    private Queue<GameRound> rounds;

    public GameLobby(Guild guild) {
        this.guild = guild;
        this.playersId = new HashSet<>();
        this.rounds = new LinkedList<>();
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
        gameList.startNewGame(playersId, rounds, guild);
    }

    public boolean isAuthor(Snowflake playerId) {
        return authorId != null && authorId.equals(playerId);
    }
}

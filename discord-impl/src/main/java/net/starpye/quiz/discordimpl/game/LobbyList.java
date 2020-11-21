package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.TextChannel;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class LobbyList {

    private Set<GameLobby> lobbies;
    private int nextId;

    public LobbyList() {
        this.lobbies = new HashSet<>();
        this.nextId = 0;
    }

    public String registerLobby(TextChannel channel, Snowflake author) {
        String id = "lobby" + nextId++;
        GameLobby lobby = new GameLobby(channel, id);
        lobby.registerAuthor(author);
        lobbies.add(lobby);
        return id;
    }

    public void unregisterLobby(GameLobby lobby) {
        lobbies.remove(lobby);
    }

    public Optional<GameLobby> findByPlayer(Snowflake playerId) {
        return lobbies
                .stream()
                .filter(lobby -> lobby.containsPlayer(playerId))
                .findAny();
    }

    public Optional<GameLobby> findByAuthor(Snowflake authorId) {
        return lobbies
                .stream()
                .filter(lobby -> lobby.isAuthor(authorId))
                .findAny();
    }

    public Optional<GameLobby> findById(String id) {
        return lobbies
                .stream()
                .filter(lobby -> lobby.isName(id))
                .findAny();
    }
}

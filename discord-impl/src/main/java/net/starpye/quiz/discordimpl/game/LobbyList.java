package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LobbyList {

    private Set<GameLobby> lobbies;

    public LobbyList() {
        this.lobbies = new HashSet<>();
    }

    public void registerLobby(GameLobby lobby) {
        lobbies.add(lobby);
    }

    public void unregisterLobby(GameLobby lobby) {
        lobbies.remove(lobby);
    }

    public Optional<GameLobby> findByAuthor(Snowflake authorId) {
        return lobbies
                .stream()
                .filter(lobby -> lobby.containsPlayer(authorId))
                .findAny();
    }
}

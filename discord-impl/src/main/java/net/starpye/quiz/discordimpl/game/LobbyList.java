package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.TextChannel;
import net.starpye.quiz.discordimpl.input.ReactionInputListener;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class LobbyList {

    private Set<GameLobby> lobbies;
    private ReactionInputListener reactionListener;
    private int nextId;

    public LobbyList(ReactionInputListener reactionListener) {
        this.reactionListener = reactionListener;
        this.lobbies = new HashSet<>();
        this.nextId = 0;
    }

    public String registerLobby(TextChannel channel, Member author) {
        String id = "lobby" + nextId++;
        GameLobby lobby = new GameLobby(channel, id);
        lobby.sendJoinImage(reactionListener);
        lobby.registerAuthor(author.getId(), author.getDisplayName());
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

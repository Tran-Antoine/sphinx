package net.starype.quiz.discordimpl.game;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.starype.quiz.discordimpl.input.ReactionInputListener;

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

    public GameLobby registerLobby(MessageChannel channel, Member author, Runnable destructLobbyCallback, String guildId) {
        String lobbyId = "lobby" + nextId++;
        GameLobby lobby = new GameLobby(channel, lobbyId, destructLobbyCallback, guildId);
        lobby.sendJoinImage(reactionListener);
        lobby.registerAuthor(author.getId(), author.getEffectiveName());
        lobbies.add(lobby);
        return lobby;
    }

    public void unregisterLobby(GameLobby lobby) {
        lobbies.remove(lobby);
    }

    public Optional<GameLobby> findByPlayer(String playerId) {
        return lobbies
                .stream()
                .filter(lobby -> lobby.containsPlayer(playerId))
                .findAny();
    }

    public Optional<GameLobby> findByAuthor(String authorId) {
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

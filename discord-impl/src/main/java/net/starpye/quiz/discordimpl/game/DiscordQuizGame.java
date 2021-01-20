package net.starpye.quiz.discordimpl.game;

import discord4j.common.util.Snowflake;
import net.starype.quiz.api.game.GameRound;
import net.starype.quiz.api.game.SimpleGame;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.ServerGate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class DiscordQuizGame extends SimpleGame<DiscordQuizGame> {

    private Set<Snowflake> votesForNext;
    private Snowflake authorId;

    public DiscordQuizGame(Queue<? extends GameRound> rounds, Collection<? extends Player<?>> players, ServerGate<DiscordQuizGame> gate, Snowflake authorId) {
        super(rounds, players);
        this.authorId = authorId;
        this.votesForNext = new HashSet<>();
        this.setGate(gate.withGame(this));
    }

    public boolean addVote(Snowflake playerId) {
        votesForNext.add(playerId);
        if(votesForNext.size() >= getPlayers().size()) {
            votesForNext.clear();
            this.nextRound();
            return true;
        }
        return false;
    }

    public boolean isAuthor(Snowflake playerId) {
        return playerId.equals(authorId);
    }
}

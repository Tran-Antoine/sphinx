package net.starype.quiz.discordimpl.game;

import net.starype.quiz.api.game.SimpleGame;
import net.starype.quiz.api.player.Player;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.server.ServerGate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

public class DiscordQuizGame extends SimpleGame<DiscordQuizGame> {

    private Set<String> votesForNext;
    private String authorId;
    private LogContainer container;

    public DiscordQuizGame(
            Queue<? extends QuizRound> rounds,
            Collection<? extends Player<?>> players,
            ServerGate<DiscordQuizGame> gate,
            String authorId,
            LogContainer container) {

        super(rounds, players);
        this.authorId = authorId;
        this.container = container;
        this.votesForNext = new HashSet<>();
        this.setGate(gate.withGame(this));
    }

    @Override
    public boolean nextRound() {
        deleteLogs();
        return super.nextRound();
    }

    public boolean addVote(String playerId, Runnable ifReady) {
        votesForNext.add(playerId);
        if(votesForNext.size() < getPlayers().size()) {
            return false;
        }
        votesForNext.clear();
        if(ifReady != null) {
            ifReady.run();
        }
        this.nextRound();
        return true;
    }

    public boolean isAuthor(String playerId) {
        return playerId.equals(authorId);
    }

    public void deleteLogs() {
        container.deleteMessages();
    }

    public void addLog(String id) {
        container.trackMessage(id);
    }
}

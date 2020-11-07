package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.server.GameServer;

import java.util.Collection;
import java.util.Queue;
import java.util.function.Consumer;

public class SimpleGame implements QuizGame {

    private Queue<? extends GameRound> rounds;
    private Collection<? extends Player> players;
    private GameServer server;
    private boolean paused;

    public SimpleGame(Queue<? extends GameRound> rounds, Collection<? extends Player> players, GameServer server) {
        this.rounds = rounds;
        this.players = players;
        this.server = server;
        this.paused = true;
    }

    @Override
    public void start() {
        this.paused = false;
        if(rounds.isEmpty()) {
            throw new IllegalStateException("Cannot start a game that has less than one round");
        }
        initHead();
    }

    @Override
    public boolean isCurrentRoundFinished() {
        if(rounds.isEmpty()) {
            return true;
        }
        return rounds.peek()
                .getContext()
                .getEndingCondition()
                .ends();
    }

    @Override
    public void nextRound() {
        rounds.poll();
        if(rounds.isEmpty()) {
            server.onGameOver();
            return;
        }
        initHead();
    }

    private void initHead() {
        rounds.element().init(this, players);
    }

    @Override
    public void onInputReceived(UUIDHolder player, String message) {
        if(paused) {
            return;
        }
        if(rounds.isEmpty()) {
            throw new IllegalStateException("Cannot accept inputs after the game is over");
        }
        GameRound current = rounds.peek();
        transferRequestToRound(player, message, current);

        GameRoundContext context = current.getContext();
        checkEndOfRound(context);
    }

    private void checkEndOfRound(GameRoundContext context) {
        if(!context.getEndingCondition().ends()) {
            return;
        }
        ScoreDistribution scoreDistribution = context.getScoreDistribution();
        for(Player player : players) {
            double score = scoreDistribution.apply(player);
            player.getScore().incrementScore(score);
            if(score > 0.001) {
                server.onPlayerScoreUpdated(player);
            }
        }
        server.onRoundEnded(context.getReportCreator(), this);
    }

    private void transferRequestToRound(UUIDHolder player, String message, GameRound current) {
        if(message.isEmpty()) {
            current.onGiveUpReceived(player);
            server.onPlayerGaveUp(player);
            return;
        }
        current.onGuessReceived(player, message);
    }

    @Override
    public void sendInputToServer(Consumer<GameServer> action) {
        if(server == null) {
            return;
        }
        action.accept(server);
    }

    @Override
    public void pause() {
        this.paused = true;
    }

    @Override
    public void resume() {
        this.paused = false;
    }

    @Override
    public void forceStop() {
        rounds.clear();
        server.onGameOver();
    }
}

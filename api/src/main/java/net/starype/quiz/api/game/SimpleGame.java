package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.event.GameEventHandler;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.server.GameServer;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class SimpleGame implements QuizGame {

    private Queue<? extends GameRound> rounds;
    private Collection<? extends Player<?>> players;
    private GameServer server;
    private final AtomicBoolean paused;
    private EventHandler eventHandler = new GameEventHandler();

    public SimpleGame(Queue<? extends GameRound> rounds, Collection<? extends Player<?>> players, GameServer server) {
        this.rounds = rounds;
        this.players = players;
        this.server = server;
        this.paused = new AtomicBoolean(true);
    }

    @Override
    public void start() {
        paused.set(false);
        if(rounds.isEmpty()) {
            throw new IllegalStateException("Cannot start a game that has less than one round");
        }
        startHead();
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
        paused.set(false);
        startHead();
    }

    private void startHead() {
        GameRound round = rounds.element();
        round.start(this, players, eventHandler);
    }

    @Override
    public void onInputReceived(IDHolder<?> player, String message) {

        if(paused.get()) {
            return;
        }
        if(rounds.isEmpty()) {
            throw new IllegalStateException("Cannot accept inputs after the game is over");
        }

        GameRound current = rounds.peek();
        GameRoundContext context = current.getContext();

        if(!context.getPlayerEligibility().isEligible(player)) {
            server.onNonEligiblePlayerGuessed(player);
            return;
        }
        transferRequestToRound(player, message, current);

        checkEndOfRound(current);
    }

    @Override
    public void checkEndOfRound(GameRound round) {
        synchronized (paused) {
            if(paused.get()) {
                return;
            }
            GameRoundContext context = round.getContext();
            if (!context.getEndingCondition().ends()) {
                return;
            }
            paused.set(true);
            updateScores(context);
            round.onRoundStopped();
            server.onRoundEnded(context.getReportCreator(), this);
        }
    }

    private void updateScores(GameRoundContext context) {
        ScoreDistribution scoreDistribution = context.getScoreDistribution();
        scoreDistribution.applyAll(players, this::updateScore);
    }

    private void updateScore(Player<?> player, double score) {
        player.getScore().incrementScore(score);
        if (Math.abs(score) > 0.001) {
            server.onPlayerScoreUpdated(player);
        }
    }

    private void transferRequestToRound(IDHolder<?> player, String message, GameRound current) {
        if(message.isEmpty()) {
            current.onGiveUpReceived(player);
            server.onPlayerGaveUp(player);
            return;
        }
        PlayerGuessContext context = current.onGuessReceived(player, message);
        server.onPlayerGuessed(context);
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
        paused.set(true);
    }

    @Override
    public void resume() {
        paused.set(false);
    }

    @Override
    public void forceStop() {
        rounds.clear();
        server.onGameOver();
    }

    @Override
    public void update() {
        eventHandler.runAllEvents();
    }

    @Override
    public boolean containsPlayerId(Object id) {
        return players
                .stream()
                .map(Player::getId)
                .anyMatch(playerId -> playerId.equals(id));
    }
}

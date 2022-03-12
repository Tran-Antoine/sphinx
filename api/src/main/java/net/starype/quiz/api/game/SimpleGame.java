package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.event.GameUpdatableHandler;
import net.starype.quiz.api.event.UpdatableHandler;
import net.starype.quiz.api.player.Player;
import net.starype.quiz.api.round.GameRound;
import net.starype.quiz.api.round.QuizRound;
import net.starype.quiz.api.server.GameServer;
import net.starype.quiz.api.server.ServerGate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Main implementation of the logic of a {@link QuizGame}.
 * <p>
 * An instance of {@code SimpleGame} holds a queue of game rounds that will be played one after the other,
 * as well as a mutable player list representing the players who participate in the game. By default, once the last
 * round is played, the game will not automatically be over but will rather wait for the next {@code nextRound} call.
 * This can be changed by modifying {@code checkEndOfRound} to make it call {@code checkEndOfGame}.
 * <p>
 * Since the class was designed to be extended by custom implementations, the {@link ServerGate} is not created
 * internally, it must be provided either by the implementation, or externally via constructor parameter or the method
 * {@link #setGate(ServerGate)}. For a completely 'finished' implementation, check out {@link DefaultSimpleGame}.
 * @param <T> the type of QuizGame the server gate works with.
 */
public class SimpleGame<T extends QuizGame> implements QuizGame {

    private ServerGate<T> gate;
    private final Queue<? extends QuizRound> rounds;
    private final Collection<? extends Player<?>> players;
    private final AtomicBoolean paused;
    private boolean waitingForNextRound;
    private final UpdatableHandler updatableHandler = new GameUpdatableHandler();
    private boolean over;

    public SimpleGame(Queue<? extends QuizRound> rounds, Collection<? extends Player<?>> players) {
        this(rounds, players, null);
    }

    public SimpleGame(Queue<? extends QuizRound> rounds, Collection<? extends Player<?>> players, ServerGate<T> gate) {
        this.rounds = rounds;
        this.players = players;
        this.paused = new AtomicBoolean(true);
        this.waitingForNextRound = false;
        this.gate = gate;
        this.over = false;
    }

    /**
     * Set the server gate for the game object.
     * @param gate the gate object that will allow interactions with the server
     */
    public void setGate(ServerGate<T> gate) {
        this.gate = gate;
    }

    @Override
    public void start() {
        Objects.requireNonNull(gate, "You must initialize the gate with the server before starting the game");
        paused.set(false);
        if(rounds.isEmpty()) {
            throw new IllegalStateException("Cannot start a game that has less than one round");
        }
        startHead(true);
    }

    @Override
    public boolean isCurrentRoundFinished() {
        if(rounds.isEmpty()) {
            return true;
        }

        QuizRound current = rounds.peek();
        if(!current.hasStarted()) {
            return false;
        }
        return current
                .getEndingCondition()
                .ends();
    }

    @Override
    public boolean nextRound() {

        if(!waitingForNextRound) {
            return false;
        }

        if(rounds.isEmpty()) {
            this.over = true;
            gate.gameCallback((server, game) -> server.onGameOver(sortPlayers(), game));
            return true;
        }

        waitingForNextRound = false;
        paused.set(false);
        updatableHandler.reset();
        startHead(false);
        return true;
    }

    private void startHead(boolean firstRound) {
        QuizRound round = rounds.element();
        gate.gameCallback((server, game) -> server.onRoundStarting(game, firstRound));
        round.start(this, players, updatableHandler);
    }

    @Override
    public void sendInput(Object playerId, String message) {

        if(paused.get()) {
            return;
        }
        if(rounds.isEmpty()) {
            throw new IllegalStateException();
        }

        QuizRound current = rounds.peek();

        Player<?> player = findHolder(playerId);

        if(!current.getPlayerEligibility().isEligible(player)) {
            gate.callback(server -> server.onNonEligiblePlayerGuessed(player));
            return;
        }
        transferRequestToRound(player, message, current);
    }

    @Override
    public void onRoundEnded(GameRound round) {
        if(paused.get()) {
            return;
        }
        rounds.poll();
        paused.set(true);
        waitingForNextRound = true;

        List<Standing> standings = updateScores(round);
        gate.gameCallback((server, game) -> server.onRoundEnded(round.getReport(standings), game));
    }

    public void checkEndOfGame() {
        if(rounds.isEmpty()) {
            gate.gameCallback((server, game) -> server.onGameOver(sortPlayers(), game));
        }
    }

    private List<Standing> updateScores(GameRound round) {
        ScoreDistribution scoreDistribution = round.getScoreDistribution();
        return scoreDistribution.applyAll(players, this::updateScore);
    }

    private void updateScore(Player<?> player, double score) {
        player.getScore().incrementScore(score);
        if (Math.abs(score) > 0.001) {
            gate.callback(server -> server.onPlayerScoreUpdated(player));
        }
    }

    private void transferRequestToRound(Player<?> player, String message, QuizRound current) {
        if(message.isEmpty()) {
            current.onGiveUpReceived(player);
            gate.callback(server -> server.onPlayerGaveUp(player));
            return;
        }
        current.onGuessReceived(player, message);
    }

    @Override
    public void sendInputToServer(Consumer<GameServer<?>> action) {
        if(gate == null) {
            return;
        }
        gate.callback(action);
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
        gate.gameCallback((server, game) -> server.onGameOver(sortPlayers(), game));
    }

    @Override
    public void update() {
        if(paused.get()) {
            return;
        }
        updatableHandler.runAllEvents();
    }

    @Override
    public boolean containsPlayerId(Object id) {
        return players
                .stream()
                .map(Player::getId)
                .anyMatch(playerId -> playerId.equals(id));
    }

    @Override
    public void removePlayer(Object playerId) {
        Optional<? extends Player<?>> optPlayer = players
                .stream()
                .filter(player -> player.getId().equals(playerId))
                .findAny();
        optPlayer.ifPresent(players::remove);
        if(!rounds.isEmpty() && !paused.get()) {
            rounds.element().checkEndOfRound();
        }
    }

    @Override
    public boolean isGameOver() {
        return over;
    }

    private List<? extends Player<?>> sortPlayers() {
        List<? extends Player<?>> players = new ArrayList<>(this.players);
        Collections.sort(players);
        return players;
    }

    private Player<?> findHolder(Object id) {
        Supplier<IllegalArgumentException> error = () -> new IllegalArgumentException("No player with given ID found");
        return players
                .stream()
                .filter(player -> player.getId().equals(id))
                .findAny()
                .orElseThrow(error);
    }

    protected QuizRound getCurrentRound() {
        return rounds.peek();
    }

    public boolean isWaitingForNextRound() {
        return waitingForNextRound;
    }

    public boolean isOutOfRounds() {
        return rounds.size() == 0;
    }
}

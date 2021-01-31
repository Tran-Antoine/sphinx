package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution.Standing;
import net.starype.quiz.api.game.event.UpdatableHandler;
import net.starype.quiz.api.game.event.GameUpdatableHandler;
import net.starype.quiz.api.game.player.Player;
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
    private Queue<? extends GameRound> rounds;
    private Collection<? extends Player<?>> players;
    private final AtomicBoolean paused;
    private boolean waitingForNextRound;
    private UpdatableHandler updatableHandler = new GameUpdatableHandler();

    public SimpleGame(Queue<? extends GameRound> rounds, Collection<? extends Player<?>> players) {
        this(rounds, players, null);
    }

    public SimpleGame(Queue<? extends GameRound> rounds, Collection<? extends Player<?>> players, ServerGate<T> gate) {
        this.rounds = rounds;
        this.players = players;
        this.paused = new AtomicBoolean(true);
        this.waitingForNextRound = false;
        this.gate = gate;
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
        return rounds.peek()
                .getContext()
                .getEndingCondition()
                .ends();
    }

    @Override
    public boolean nextRound() {

        if(!waitingForNextRound) {
            return false;
        }

        if(rounds.isEmpty()) {
            gate.gameCallback((server, game) -> server.onGameOver(sortPlayers(), game));
            return true;
        }

        paused.set(false);
        startHead(false);
        return true;
    }

    private void startHead(boolean firstRound) {
        GameRound round = rounds.element();
        gate.gameCallback((server, game) -> server.onRoundStarting(game, firstRound));
        round.start(this, players, updatableHandler);
    }

    @Override
    public void onInputReceived(Object playerId, String message) {

        if(paused.get()) {
            return;
        }
        if(rounds.isEmpty()) {
            throw new IllegalStateException();
        }

        GameRound current = rounds.peek();
        GameRoundContext context = current.getContext();

        Player<?> player = findHolder(playerId);

        if(!context.getPlayerEligibility().isEligible(player)) {
            gate.callback(server -> server.onNonEligiblePlayerGuessed(player));
            return;
        }
        transferRequestToRound(player, message, current);

        checkEndOfRound(current);
    }

    @Override
    public void checkEndOfRound(GameRound current) {
        synchronized (paused) {
            if(paused.get()) {
                return;
            }
            GameRoundContext context = current.getContext();
            if (!context.getEndingCondition().ends()) {
                return;
            }

            rounds.poll();
            paused.set(true);
            waitingForNextRound = true;

            List<Standing> standings = updateScores(context);
            current.onRoundStopped();
            gate.gameCallback((server, game) -> server.onRoundEnded(context.getReportCreator(standings), game));
        }
    }

    public void checkEndOfGame() {
        if(rounds.isEmpty()) {
            gate.gameCallback((server, game) -> server.onGameOver(sortPlayers(), game));
        }
    }

    private List<Standing> updateScores(GameRoundContext context) {
        ScoreDistribution scoreDistribution = context.getScoreDistribution();
        return scoreDistribution.applyAll(players, this::updateScore);
    }

    private void updateScore(Player<?> player, double score) {
        player.getScore().incrementScore(score);
        if (Math.abs(score) > 0.001) {
            gate.callback(server -> server.onPlayerScoreUpdated(player));
        }
    }

    private void transferRequestToRound(Player<?> player, String message, GameRound current) {
        if(message.isEmpty()) {
            current.onGiveUpReceived(player);
            gate.callback(server -> server.onPlayerGaveUp(player));
            return;
        }
        PlayerGuessContext context = current.onGuessReceived(player, message);
        gate.callback(server -> server.onPlayerGuessed(context));
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

    protected Collection<? extends Player<?>> getPlayers() {
        return players;
    }

    public boolean isWaitingForNextRound() {
        return waitingForNextRound;
    }

    public boolean isOutOfRounds() {
        return rounds.size() == 0;
    }
}

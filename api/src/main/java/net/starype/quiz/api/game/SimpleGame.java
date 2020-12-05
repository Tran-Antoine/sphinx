package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.event.GameEventHandler;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.server.GameServer;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SimpleGame implements QuizGame {

    private Queue<? extends GameRound> rounds;
    private Collection<? extends Player<?>> players;
    private GameServer<? super QuizGame> server;
    private final AtomicBoolean paused;
    private boolean waitingForNextRound;
    private EventHandler eventHandler = new GameEventHandler();

    public SimpleGame(Queue<? extends GameRound> rounds, Collection<? extends Player<?>> players, GameServer<? super QuizGame> server) {
        this.rounds = rounds;
        this.players = players;
        this.server = server;
        this.paused = new AtomicBoolean(true);
        this.waitingForNextRound = false;
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
    public boolean nextRound() {

        if(!waitingForNextRound) {
            return false;
        }

        rounds.poll();
        if(rounds.isEmpty()) {
            server.onGameOver(this, sortPlayers());
            return false;
        }

        paused.set(false);
        startHead();
        return true;
    }

    private void startHead() {
        GameRound round = rounds.element();
        round.start(this, players, eventHandler);
    }

    @Override
    public void onInputReceived(Object playerId, String message) {

        if(paused.get()) {
            return;
        }
        if(rounds.isEmpty()) {
            throw new IllegalStateException("Cannot accept inputs after the game is over");
        }

        GameRound current = rounds.peek();
        GameRoundContext context = current.getContext();

        Player<?> player = findHolder(playerId);

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
            waitingForNextRound = true;

            Map<Player<?>, Double> standings = updateScores(context);
            round.onRoundStopped();
            server.onRoundEnded(context.getReportCreator(standings), this);
        }
    }

    private Map<Player<?>, Double> updateScores(GameRoundContext context) {
        ScoreDistribution scoreDistribution = context.getScoreDistribution();
        return scoreDistribution.applyAll(players, this::updateScore);
    }

    private void updateScore(Player<?> player, double score) {
        player.getScore().incrementScore(score);
        if (Math.abs(score) > 0.001) {
            server.onPlayerScoreUpdated(player);
        }
    }

    private void transferRequestToRound(Player<?> player, String message, GameRound current) {
        if(message.isEmpty()) {
            current.onGiveUpReceived(player);
            server.onPlayerGaveUp(player);
            return;
        }
        PlayerGuessContext context = current.onGuessReceived(player, message);
        server.onPlayerGuessed(context);
    }

    @Override
    public void sendInputToServer(Consumer<GameServer<?>> action) {
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
        server.onGameOver(this, sortPlayers());
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
}

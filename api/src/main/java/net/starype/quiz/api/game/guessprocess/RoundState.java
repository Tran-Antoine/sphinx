package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.PlayerGuessContext;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoundState {

    //TODO : Maybe change the Player type into an IDHolder ?
    /**
     * HashMap that maps every player to its current correctness for this round
     */
    private Map<Player<?>, Double> roundCorrectness = new HashMap<>();

    /**
     * Counter of guesses for this round
     */
    private MaxGuessCounter counter;

    /**
     * Context of the current player who is guessing
     */
    private PlayerGuessContext playerGuessContext;


    public RoundState(Collection<? extends Player<?>> players, MaxGuessCounter counter) {
        for (Player<?> player : players) {
            roundCorrectness.put(player, 0.0);
        }
        this.counter = counter;
    }

    public RoundState(Map<Player<?>, Double> initialStandings, MaxGuessCounter counter) {
        roundCorrectness = initialStandings;
        this.counter = counter;
    }

//    public void incrementCurrentPlayerGuess() {
//        counter.incrementGuess(playerGuessContext.getPlayer());
//    }
//
//    public void consumeAllGuesses() {
//        for(IDHolder<?> player : roundCorrectness.keySet()) {
//            counter.consumeAllGuesses(player);
//        }
//    }
//
//    public void updateCurrentPlayerCorrectness() {
//        roundCorrectness.replace(playerGuessContext.getPlayer(), playerGuessContext.getCorrectness());
//    }

    public void setPlayerGuessContext(PlayerGuessContext playerGuessContext) {
        this.playerGuessContext = playerGuessContext;
    }

    public MaxGuessCounter getCounter() {
        return counter;
    }

    public PlayerGuessContext getPlayerGuessContext() {
        return playerGuessContext;
    }

    public Map<? extends IDHolder<?>, Double> getLeaderboard() {
        return roundCorrectness;
    }

    public void setLeaderboard(Player<?> player, double correctness) {
        if(roundCorrectness.containsKey(player)) {
            roundCorrectness.replace(player, correctness);
        } else {
            roundCorrectness.put(player, correctness);
        }
    }
}

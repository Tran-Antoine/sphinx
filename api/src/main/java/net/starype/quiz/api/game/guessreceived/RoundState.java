package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.EntityEligibility;
import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoundState {

    /**
     * HashMap that maps every player to its current correctness for this round
     */
    private Map<Player<?>, Double> roundCorrectness = new HashMap<>();

    /**
     * Counter of guesses for this round
     */
    private MaxGuessCounter counter;

    /**
     * Answers of all players of this round
     */
    private Map<Player<?>, Answer> answers = new HashMap<>();

    /**
     * Eligibility of the player
     */
    private EntityEligibility playerEligibility;

    /**
     * Constructor of RoundState
     * @param players Players of the round (doesn't count teams)
     * @param counter MaxGuessCounter of the round
     * @param playerEligibility PlayerEligibility of the Round
     */
    public RoundState(Collection<? extends Player<?>> players, MaxGuessCounter counter,
                      EntityEligibility playerEligibility) {
        for (Player<?> player : players) {
            roundCorrectness.put(player, 0.0);
        }
        this.counter = counter;
        this.playerEligibility = playerEligibility;
    }

    /**
     * @return the MaxGuessCounter
     */
    public MaxGuessCounter getCounter() {
        return counter;
    }

    /**
     * @return the Map which links evey players to its current correctness
     */
    public Map<Player<?>, Double> getLeaderboard() {
        return roundCorrectness;
    }

    public void updateLeaderboard(Player<?> player, double correctness) {
        if (roundCorrectness.containsKey(player)) {
            roundCorrectness.replace(player, correctness);
        } else {
            roundCorrectness.put(player, correctness);
        }
    }
    
    public void addCorrectnessIfNew(Player<?> player, double correctness) {
        roundCorrectness.putIfAbsent(player, correctness);
    }

    public void updateRoundAnswers(Player<?> player, Answer answer) {
        if(answers.containsKey(player)) {
            answers.replace(player, answer);
        } else {
            answers.put(player, answer);
        }
    }

    public boolean isPlayerEligible(IDHolder<?> player) {
        return playerEligibility.isEligible(player);
    }

    public void addAnswerIfNew(Player<?> player, Answer answer) {
        answers.putIfAbsent(player, answer);
    }


}

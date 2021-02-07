package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.EntityEligibility;
import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.PlayersSettable;
import net.starype.quiz.api.game.answer.Answer;
import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class RoundState implements PlayersSettable {

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
     * @param counter MaxGuessCounter of the round
     * @param playerEligibility PlayerEligibility of the Round
     */
    public RoundState(MaxGuessCounter counter,
                      EntityEligibility playerEligibility) {
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

    /**
     * Add the player's correctness to leaderboard if there is no correctness linked to the current player in leaderboard.
     * Else replace te old correctness.
     * @param player the current player
     * @param correctness the correctness of the player's answer
     */
    public void updateLeaderboard(Player<?> player, double correctness) {
        if (roundCorrectness.containsKey(player)) {
            roundCorrectness.replace(player, correctness);
        } else {
            roundCorrectness.put(player, correctness);
        }
    }

    /**
     * Add a correctness to the leaderboard if there is no correctness linked to the current player in the leaderboard
     * @param player the current player
     * @param correctness the correctness corresponding to the current player
     */
    public void addCorrectnessIfNew(Player<?> player, double correctness) {
        roundCorrectness.putIfAbsent(player, correctness);
    }

    /**
     * Add the player's answer to answers if there is no answer linked to the current player in answers.
     * Else replace te old answer.
     * @param player the current player
     * @param answer the answer corresponding to the current player
     */
    public void updateRoundAnswers(Player<?> player, Answer answer) {
        if(answers.containsKey(player)) {
            answers.replace(player, answer);
        } else {
            answers.put(player, answer);
        }
    }

    /**
     * @param player the player
     * @return if the player is eligible or not
     */
    public boolean isPlayerEligible(IDHolder<?> player) {
        return playerEligibility.isEligible(player);
    }

    /**
     * Add a correctness to answers if there is no answer linked to the current player in answers
     * @param player the current player
     * @param answer the answer corresponding to the current player
     */
    public void addAnswerIfNew(Player<?> player, Answer answer) {
        answers.putIfAbsent(player, answer);
    }


    @Override
    public void setPlayers(Collection<? extends Player<?>> players) {
        for (Player<?> player : players) {
            roundCorrectness.put(player, 0.0);
        }
    }
}

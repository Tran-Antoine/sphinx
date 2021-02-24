package net.starype.quiz.api.round;

import net.starype.quiz.api.game.MutableGuessContext;
import net.starype.quiz.api.game.ScoreDistribution;

/**
 * BiPredicate that tests if the correctness of the answer is 0.0
 */
public class IsCorrectnessZero implements GuessReceivedPredicate {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness()) < ScoreDistribution.EPSILON;
    }
}

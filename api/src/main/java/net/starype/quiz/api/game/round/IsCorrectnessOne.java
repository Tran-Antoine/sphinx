package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * BiPredicate that tests if the correctness of the answer is 1
 */
public class IsCorrectnessOne implements GuessReceivedPredicate {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness() - 1) < 0.001;
    }
}

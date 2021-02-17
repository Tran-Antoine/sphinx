package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

/**
 * BiPredicate that tests if the correctness of the answer is 0.0
 */
public class IsCorrectnessZero implements BiPredicate<RoundState, MutableGuessContext> {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness()) < 0.001;
    }
}

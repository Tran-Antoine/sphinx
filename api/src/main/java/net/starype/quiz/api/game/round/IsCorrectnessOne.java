package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

/**
 * BiPredicate that tests if the correctness of the answer is 1
 */
public class IsCorrectnessOne implements BiPredicate<RoundState, MutableGuessContext> {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness() - 1) < 0.001;
    }
}

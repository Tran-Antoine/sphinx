package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.PlayerGuessContext;

import java.util.function.BiPredicate;

/**
 * BiPredicate that tests if the correctness of the answer is 0.0
 */
public class IsCorrectnessZero implements BiPredicate<RoundState, PlayerGuessContext> {

    @Override
    public boolean test(RoundState roundState, PlayerGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness()) < 0.001;
    }
}

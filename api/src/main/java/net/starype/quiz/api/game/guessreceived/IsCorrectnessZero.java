package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.PlayerGuessContext;

import java.util.function.BiPredicate;

public class IsCorrectnessZero implements BiPredicate<RoundState, PlayerGuessContext> {

    @Override
    public boolean test(RoundState roundState, PlayerGuessContext playerGuessContext) {
        //TODO : Replace 0.001 by EPSILON
        return Math.abs(playerGuessContext.getCorrectness() - 1) < 0.001;
    }
}

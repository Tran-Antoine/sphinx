package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.PlayerGuessContext;

import java.util.function.BiPredicate;

public class IsAnswerCorrect implements BiPredicate<RoundState, PlayerGuessContext> {

    @Override
    public boolean test(RoundState roundState, PlayerGuessContext playerGuessContext) {
        return Math.abs(playerGuessContext.getCorrectness()) < 0.001;
    }
}

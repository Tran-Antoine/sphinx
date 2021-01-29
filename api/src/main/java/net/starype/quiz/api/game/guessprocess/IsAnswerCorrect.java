package net.starype.quiz.api.game.guessprocess;

import java.util.function.Predicate;

public class IsAnswerCorrect implements Predicate<RoundState> {

    @Override
    public boolean test(RoundState roundState) {
        return Math.abs(roundState.getPlayerGuessContext().getCorrectness()) < 0.001;
    }
}

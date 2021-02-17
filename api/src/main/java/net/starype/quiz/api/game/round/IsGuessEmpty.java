package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * This predicate is set to true if the guess is empty, it is set
 * to false if not.
 */
public class IsGuessEmpty implements GuessReceivedPredicate {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext guessContext) {
        return !guessContext.isAnswerValid();
    }
}

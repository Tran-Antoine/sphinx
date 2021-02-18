package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * This predicate is set to true if the guess is valid, it is set
 * to false if not.
 */
public class IsGuessValid implements GuessReceivedPredicate {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext guessContext) {
        return !guessContext.isAnswerValid();
    }
}

package net.starype.quiz.api.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * BiPredicate that tests if the guess is valid or not
 */
public class IsGuessValid implements GuessReceivedPredicate {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext guessContext) {
        return guessContext.isAnswerValid();
    }
}

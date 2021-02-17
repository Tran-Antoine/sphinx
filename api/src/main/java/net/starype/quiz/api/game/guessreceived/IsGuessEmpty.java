package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

/**
 * This predicate is set to true if the guess is empty, it is set
 * to false if not.
 */
public class IsGuessEmpty implements BiPredicate<RoundState, MutableGuessContext> {

    @Override
    public boolean test(RoundState roundState, MutableGuessContext guessContext) {
        return !guessContext.isAnswerValid();
    }
}

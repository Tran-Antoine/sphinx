package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

import java.util.function.BiPredicate;

/**
 * BiPredicate that tests if the current guessing player is eligible or not
 */
public class IsPlayerEligible implements BiPredicate<RoundState, SettablePlayerGuessContext> {

    @Override
    public boolean test(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        return roundState.isPlayerEligible(playerGuessContext.getPlayer());
    }
}

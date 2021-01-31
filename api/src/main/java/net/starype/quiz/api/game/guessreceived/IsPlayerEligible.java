package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

import java.util.function.BiPredicate;

public class IsPlayerEligible implements BiPredicate<RoundState, SettablePlayerGuessContext> {

    @Override
    public boolean test(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        return roundState.isPlayerEligible(playerGuessContext.getPlayer());
    }
}

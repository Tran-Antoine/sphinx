package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

/**
 * ConditionalConsumer that makes the Player eligible
 */
public class MakePlayerEligible extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {

    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(true);
    }
}

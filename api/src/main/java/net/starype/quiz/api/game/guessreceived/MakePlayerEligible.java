package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * ConditionalConsumer that makes the Player eligible
 */
public class MakePlayerEligible extends ConditionalConsumer {

    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(true);
    }
}

package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * SimpleReceivedAction that makes the Player eligible
 */
public class MakePlayerEligible extends SimpleReceivedAction {

    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(true);
    }
}

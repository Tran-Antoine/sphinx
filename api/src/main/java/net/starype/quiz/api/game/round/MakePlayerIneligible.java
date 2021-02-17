package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * SimpleReceivedAction that make the current guessing player not eligible
 */
public class MakePlayerIneligible extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(false);
    }
}

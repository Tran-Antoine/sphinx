package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * SimpleReceivedAction that make the current guessing player not eligible
 */
public class MakePlayerNotEligible extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(false);
    }
}

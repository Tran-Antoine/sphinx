package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * ConditionalConsumer that make the current guessing player not eligible
 */
public class MakePlayerNotEligible extends ConditionalConsumer{
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setEligibility(false);
    }
}

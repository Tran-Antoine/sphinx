package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * ConditionalConsumer that set the current player correctness to 0.0
 */
public class InvalidateCurrentPlayerCorrectness extends ConditionalConsumer {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setCorrectness(0.0);
    }
}

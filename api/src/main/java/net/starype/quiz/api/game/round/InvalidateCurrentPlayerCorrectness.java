package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MutableGuessContext;

/**
 * SimpleReceivedAction that set the current player correctness to 0.0
 */
public class InvalidateCurrentPlayerCorrectness extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        playerGuessContext.setCorrectness(0.0);
    }
}

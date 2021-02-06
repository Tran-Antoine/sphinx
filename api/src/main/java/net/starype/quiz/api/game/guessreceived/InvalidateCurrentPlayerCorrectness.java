package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

/**
 * ConditionalConsumer that set the current player correctness to 0.0
 */
public class InvalidateCurrentPlayerCorrectness extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        playerGuessContext.setCorrectness(0.0);
    }
}

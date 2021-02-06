package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

public class InvalidateCurrentPlayerCorrectness extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        playerGuessContext.setCorrectness(0.0);
    }
}

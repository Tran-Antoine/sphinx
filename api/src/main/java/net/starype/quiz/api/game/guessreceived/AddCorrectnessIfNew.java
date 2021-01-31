package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

public class AddCorrectnessIfNew extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        roundState.addCorrectnessIfNew(playerGuessContext.getPlayer(), playerGuessContext.getCorrectness());
    }
}

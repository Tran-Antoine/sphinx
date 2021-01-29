package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.SettablePlayerGuessContext;

public class ConsumePlayerGuess extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        counter.consumeAllGuesses(playerGuessContext.getPlayer());
    }
}

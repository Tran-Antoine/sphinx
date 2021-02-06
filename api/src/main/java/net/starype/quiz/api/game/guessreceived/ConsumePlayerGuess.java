package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.SettablePlayerGuessContext;

/**
 * ConditionalConsumer that consume every guesses of the current player who is guessing
 */
public class ConsumePlayerGuess extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        counter.consumeAllGuesses(playerGuessContext.getPlayer());
    }
}

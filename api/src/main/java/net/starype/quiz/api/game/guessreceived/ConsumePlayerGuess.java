package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.MutableGuessContext;

/**
 * ConditionalConsumer that consume every guesses of the current player who is guessing
 */
public class ConsumePlayerGuess extends ConditionalConsumer {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        counter.consumeAllGuesses(playerGuessContext.getPlayer());
    }
}

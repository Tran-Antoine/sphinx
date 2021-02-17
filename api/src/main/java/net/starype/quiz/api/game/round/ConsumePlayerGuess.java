package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.MutableGuessContext;

/**
 * SimpleReceivedAction that consume every guesses of the current player who is guessing
 */
public class ConsumePlayerGuess extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        counter.consumeAllGuesses(playerGuessContext.getPlayer());
    }
}

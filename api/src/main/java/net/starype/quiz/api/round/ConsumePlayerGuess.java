package net.starype.quiz.api.round;

import net.starype.quiz.api.game.GuessCounter;
import net.starype.quiz.api.game.MutableGuessContext;

/**
 * SimpleReceivedAction that consume every guesses of the current player who is guessing
 */
public class ConsumePlayerGuess extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        GuessCounter counter = roundState.getCounter();
        counter.consumeAllGuesses(playerGuessContext.getPlayer());
    }
}

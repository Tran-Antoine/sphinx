package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.MaxGuessCounter;

public class ConsumePlayerGuess extends ConditionalConsumer<RoundState> {
    @Override
    public void execute(RoundState roundState) {
        MaxGuessCounter counter = roundState.getCounter();
        counter.consumeAllGuesses(roundState.getPlayerGuessContext().getPlayer());
    }
}

package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

/**
 * ConditionalConsumer that consumes every guesses from every players of the round
 */
public class ConsumeAllPlayersGuess extends ConditionalConsumer<RoundState, SettablePlayerGuessContext> {
    @Override
    public void execute(RoundState roundState, SettablePlayerGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        Collection<? extends IDHolder<?>> players = roundState.getPlayers();
        for(IDHolder<?> player : players) {
            counter.consumeAllGuesses(player);
        }
    }
}

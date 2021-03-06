package net.starype.quiz.api.round;

import net.starype.quiz.api.game.GuessCounter;
import net.starype.quiz.api.game.MutableGuessContext;
import net.starype.quiz.api.player.IDHolder;

import java.util.Collection;

/**
 * SimpleReceivedAction that consumes every guesses from every players of the round
 */
public class ConsumeAllPlayersGuess extends SimpleReceivedAction {
    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        GuessCounter counter = roundState.getCounter();
        Collection<? extends IDHolder<?>> players = roundState.getPlayers();
        for(IDHolder<?> player : players) {
            counter.consumeAllGuesses(player);
        }
    }
}

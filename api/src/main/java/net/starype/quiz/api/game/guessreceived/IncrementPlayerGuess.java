package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MaxGuessCounter;
import net.starype.quiz.api.game.MutableGuessContext;
import net.starype.quiz.api.game.player.Player;

/**
 * SimpleReceivedAction that increments the number of guesses of current Player
 */
public class IncrementPlayerGuess extends SimpleReceivedAction {

    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        MaxGuessCounter counter = roundState.getCounter();
        Player<?> player = playerGuessContext.getPlayer();
        counter.incrementGuess(player);
    }
}

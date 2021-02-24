package net.starype.quiz.api.round;

import net.starype.quiz.api.game.GuessCounter;
import net.starype.quiz.api.game.MutableGuessContext;
import net.starype.quiz.api.player.Player;

/**
 * SimpleReceivedAction that increments the number of guesses of current Player
 */
public class IncrementPlayerGuess extends SimpleReceivedAction {

    @Override
    public void execute(RoundState roundState, MutableGuessContext playerGuessContext) {
        GuessCounter counter = roundState.getCounter();
        Player<?> player = playerGuessContext.getPlayer();
        counter.incrementGuess(player);
    }
}

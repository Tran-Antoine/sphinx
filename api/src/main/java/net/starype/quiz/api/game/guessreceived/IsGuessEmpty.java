package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

/**
 * GuessReceivedHead that is linked to a predicate. This predicate is set to true if the guess is empty, it is set
 * to false if not.
 */
public class IsGuessEmpty extends GuessReceivedHead {
    @Override
    public void accept(Player<?> player, String message, Double correctness, RoundState roundState,
                       SettablePlayerGuessContext playerGuessContext) {
        if (correctness == null) {
            setToTrue();
        } else {
            setToFalse();
        }
    }
}

package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.GuessReceivedParameters;
import net.starype.quiz.api.game.player.Player;

import java.util.function.Consumer;

/**
 * The first function called in {@link net.starype.quiz.api.game.StandardRound#onGuessReceived(Player, String)}
 * This function process the answer given by the player and the information related to this answer such as
 * the correctness, the current {@link RoundState}, etc.
 * This function is generally called before the {@link ConditionalConsumer}.
 */
public abstract class GuessReceivedHead implements Consumer<GuessReceivedParameters>,
        BooleanController {

    boolean controlledBoolean = false;

    public void setControlledBoolean(boolean newValue) {
        controlledBoolean = newValue;
    }

    @Override
    public boolean value() {
        return controlledBoolean;
    }
}

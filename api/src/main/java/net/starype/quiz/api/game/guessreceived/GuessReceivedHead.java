package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

/**
 * The first function called in {@link net.starype.quiz.api.game.StandardRound#onGuessReceived(Player, String)}
 * This function process the answer given by the player and the information related to this answer such as
 * the correctness, the current {@link RoundState}, etc.
 * This function is generally called before the {@link ConditionalConsumer}.
 */
public abstract class GuessReceivedHead implements BooleanController,
        PentaConsumer<Player<?>, String, Double, RoundState, SettablePlayerGuessContext> {

    boolean controlledBoolean = false;

    @Override
    public abstract void accept(Player<?> player, String message, Double correctness,
                                 RoundState roundState, SettablePlayerGuessContext playerGuessContext);

    @Override
    public boolean value() {
        return controlledBoolean;
    }

    @Override
    public void setToTrue() {
        controlledBoolean = true;
    }

    @Override
    public void setToFalse() {
        controlledBoolean = false;
    }

}

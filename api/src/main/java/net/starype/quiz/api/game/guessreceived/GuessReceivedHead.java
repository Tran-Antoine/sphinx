package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The first function called in {@link net.starype.quiz.api.game.StandardRound#onGuessReceived(Player, String)}
 * This function process the answer given by the player and the information related to this answer such as
 * the correctness, the current {@link RoundState}, etc.
 * This function is generally called before the {@link ConditionalConsumer}.
 */
public abstract class GuessReceivedHead extends AtomicBoolean implements
        PentaConsumer<Player<?>, String, Double, RoundState, SettablePlayerGuessContext> {

    @Override
    public abstract void accept(Player<?> player, String message, Double correctness,
                                 RoundState roundState, SettablePlayerGuessContext playerGuessContext);

}

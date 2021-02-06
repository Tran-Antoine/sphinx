package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

import java.util.function.BiPredicate;

/**
 * The first function called in {@link net.starype.quiz.api.game.StandardRound#onGuessReceived(Player, String)}
 * This function process the answer given by the player and the information related to this answer such as
 * the correctness, the current {@link RoundState}, etc.
 * This function is generally called before the {@link ConditionalConsumer}.
 */
public abstract class GuessReceivedHead implements BiPredicateController<RoundState, SettablePlayerGuessContext>,
        PentaConsumer<Player<?>, String, Double, RoundState, SettablePlayerGuessContext> {

    BiPredicate<RoundState, SettablePlayerGuessContext> isExecuted;

    public abstract void accept(Player<?> player, String message, Double correctness,
                                RoundState roundState, SettablePlayerGuessContext playerGuessContext);

    @Override
    public GuessReceivedHead control(BiPredicate<RoundState, SettablePlayerGuessContext> predicate) {
        isExecuted = predicate;
        isExecuted = (t, u) -> false;
        return this;
    }

    @Override
    public void setToTrue() {
        isExecuted = (t, u) -> true;
    }

    @Override
    public void setToFalse() {
        isExecuted = (t, u) -> false;
    }

}

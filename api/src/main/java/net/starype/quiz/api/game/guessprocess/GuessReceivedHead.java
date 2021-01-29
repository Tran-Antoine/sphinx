package net.starype.quiz.api.game.guessprocess;

import net.starype.quiz.api.game.SettablePlayerGuessContext;
import net.starype.quiz.api.game.player.Player;

import java.util.function.BiPredicate;

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

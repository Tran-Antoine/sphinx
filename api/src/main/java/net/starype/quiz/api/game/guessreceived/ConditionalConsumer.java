package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.SettablePlayerGuessContext;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiPredicate;

/**
 * A BiConsumer that is linked to a BiPredicate, and controls another boolean. By default,
 * this ConditionalConsumer will execute only if the bounded BiPredicate is true. The ConditionalConsumer
 * may also control the value of a boolean to enable/disable the execution of other ConditionalConsumer.
 */
public abstract class ConditionalConsumer extends AtomicBoolean implements GuessReceivedAction,
        BiPredicateBounded<RoundState, SettablePlayerGuessContext> {
    /**
     * The bounded predicate of this ConditionalConsumer. This consumer will execute only if this bounded predicate
     * is true.
     */
    private BiPredicate<RoundState, SettablePlayerGuessContext> boundedPredicate = (t, u) -> true;

    /**
     * the AtomicBoolean controlled by this consumer
     */
    private boolean controlledBoolean = false;


    @Override
    public void accept(RoundState t, SettablePlayerGuessContext u) {
        if (boundedPredicate.test(t, u)) {
            execute(t, u);
        }
    }

    public abstract void execute(RoundState t, SettablePlayerGuessContext u);

    @Override
    public ConditionalConsumer linkTo(BiPredicate<RoundState, SettablePlayerGuessContext> biPredicate) {
        boundedPredicate = biPredicate;
        return this;
    }

}

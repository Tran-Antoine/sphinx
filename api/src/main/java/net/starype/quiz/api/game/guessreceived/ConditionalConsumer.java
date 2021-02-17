package net.starype.quiz.api.game.guessreceived;

import net.starype.quiz.api.game.MutableGuessContext;

import java.util.function.BiPredicate;

/**
 * A BiConsumer that is linked to a BiPredicate, and controls another boolean. By default,
 * this ConditionalConsumer will execute only if the bounded BiPredicate is true. The ConditionalConsumer
 * may also control the value of a boolean to enable/disable the execution of other ConditionalConsumer.
 */
public abstract class ConditionalConsumer implements GuessReceivedAction {

    /**
     * The bounded predicate of this ConditionalConsumer. This consumer will execute only if this bounded predicate
     * is true.
     */
    private BiPredicate<RoundState, MutableGuessContext> boundedPredicate = (t, u) -> true;

    boolean controlledBoolean = false;

    public abstract void execute(RoundState t, MutableGuessContext u);

    @Override
    public void accept(RoundState t, MutableGuessContext u) {
        if (boundedPredicate.test(t, u)) {
            execute(t, u);
        }
    }

    public boolean value() {
        return controlledBoolean;
    }

    public ConditionalConsumer linkTo(BiPredicate<RoundState,
            MutableGuessContext> biPredicate) {
        boundedPredicate = biPredicate;
        return this;
    }

    public void setControlledBoolean(boolean newValue) {
        controlledBoolean = newValue;
    }
}

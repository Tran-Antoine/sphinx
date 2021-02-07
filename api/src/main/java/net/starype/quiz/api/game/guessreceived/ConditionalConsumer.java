package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

/**
 * A BiConsumer that is linked to a BiPredicate, and controls another BiPredicate. By default,
 * this ConditionalConsumer will execute only if the bounded BiPredicate is true. The ConditionalConsumer
 * may also control the value of a boolean to enable/disable the execution of other ConditionalConsumer.
 * @param <T> the type of the first parameter of the ConditionalConsumer
 * @param <U> the type of the second parameter of the ConditionalConsumer
 */
public abstract class ConditionalConsumer<T, U> implements BiConsumer<T, U>,
        BooleanController, BiPredicateBounded<T, U> {
    /**
     * The bounded predicate of this ConditionalConsumer. This consumer will execute only if this bounded predicate
     * is true.
     */
    BiPredicate<T, U> boundedPredicate = (t, u) -> true;

    /**
     * the AtomicBoolean controlled by this consumer
     */
    boolean controlledBoolean = false;


    @Override
    public void accept(T t, U u) {
        if (boundedPredicate.test(t, u)) {
            execute(t, u);
        }
    }

    public abstract void execute(T t, U u);

    @Override
    public ConditionalConsumer<T, U> linkTo(BiPredicate<T, U> biPredicate) {
        boundedPredicate = biPredicate;
        return this;
    }

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

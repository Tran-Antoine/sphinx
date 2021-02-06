package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public abstract class ConditionalConsumer<T, U> implements BiConsumer<T, U>,
        BiPredicateController<T, U>, BiPredicateBounded<T, U> {
    BiPredicate<T, U> boundedCondition = (t, u) -> true;
    BiPredicate<T, U> controlledPredicate = (t, u) -> false;

    @Override
    public void accept(T t, U u) {
        if (boundedCondition.test(t, u)) {
            execute(t, u);
        }
    }

    public abstract void execute(T t, U u);

    @Override
    public ConditionalConsumer<T, U> linkTo(BiPredicate<T, U> predicate) {
        boundedCondition = predicate;
        return this;
    }

    @Override
    public ConditionalConsumer<T, U> control(BiPredicate<T, U> predicate) {
        controlledPredicate = predicate;
        return this;
    }

    @Override
    public void setToTrue() {
        controlledPredicate = (t, u) -> true;
    }

    @Override
    public void setToFalse() {
        controlledPredicate = (t, u) -> false;
    }
}

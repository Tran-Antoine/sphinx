package net.starype.quiz.api.game.guessprocess;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public abstract class ConditionalConsumer<T, U> implements BiConsumer<T, U>,
        BiPredicateController<T, U>, BiPredicateBounded<T, U> {
    BiPredicate<T, U> boundedCondition = (t, u) -> true;
    BiPredicate<T, U> isExecuted = (t, u) -> false;

    @Override
    public void accept(T t, U u) {
        if(boundedCondition.test(t, u)) {
            execute(t, u);
            isExecuted = (v, w) -> true;
        }
    }

    public BiPredicate<T, U> getIsExecuted() {
        return isExecuted;
    }

    public abstract void execute(T t, U u);

    @Override
    public ConditionalConsumer<T, U> linkTo(BiPredicate<T, U> predicate) {
        boundedCondition = predicate;
        return this;
    }

    @Override
    public ConditionalConsumer<T, U> control(BiPredicate<T, U> predicate) {
        isExecuted = predicate;
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

package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public abstract class ConditionalBiConsumer<T, U> implements BiConsumer<T, U>,
        BiPredicateBounded<T, U> {
    BiPredicate<T, U> boundedCondition = (t, u) -> true;
    BiPredicate<T, U> isExecuted = (t, u) -> false;

    @Override
    public void accept(T t, U u) {
        if (boundedCondition.test(t, u)) {
            execute(t, u);
            isExecuted = (v, w) -> true;
        }
    }

    public BiPredicate<T, U> getIsExecuted() {
        return isExecuted;
    }

    public abstract void execute(T t, U u);

    @Override
    public ConditionalBiConsumer<T, U> linkTo(BiPredicate<T, U> predicate) {
        boundedCondition = predicate;
        return this;
    }
    
}

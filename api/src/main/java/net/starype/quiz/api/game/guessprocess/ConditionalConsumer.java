package net.starype.quiz.api.game.guessprocess;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ConditionalConsumer<T> implements Consumer<T>, PredicateController<T>, PredicateBounded<T> {
    Predicate<T> boundedCondition = t -> true;
    Predicate<T> isExecuted = t -> false;

    @Override
    public void accept(T t) {
        if(boundedCondition.test(t)) {
            execute(t);
            isExecuted = v -> true;
        }
    }

    public Predicate<T> getIsExecuted() {
        return isExecuted;
    }

    public abstract void execute(T t);

    @Override
    public ConditionalConsumer<T> linkTo(Predicate<T> predicate) {
        boundedCondition = predicate;
        return this;
    }

    @Override
    public ConditionalConsumer<T> control(Predicate<T> predicate) {
        isExecuted = predicate;
        return this;
    }

    @Override
    public void setToTrue() {
        isExecuted = t -> true;
    }

    @Override
    public void setToFalse() {
        isExecuted = t -> false;
    }
}

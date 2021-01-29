package net.starype.quiz.api.game.guessprocess;

import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ConditionalConsumer<T> implements Consumer<T>, PredicateController<T>, PredicateBounded<T> {
    Predicate<T> boundedCondition = t -> true;
    Predicate<T> controlledCondition;

    @Override
    public void accept(T t) {
        if(boundedCondition.test(t)) {
            execute(t);
        }
    }

    public abstract void execute(T t);

    @Override
    public void linkTo(Predicate<T> predicate) {
        boundedCondition = predicate;
    }

    @Override
    public void control(Predicate<T> predicate) {
        controlledCondition = predicate;
    }

    @Override
    public void setToTrue() {
        controlledCondition = t -> true;
    }

    @Override
    public void setToFalse() {
        controlledCondition = t -> false;
    }
}

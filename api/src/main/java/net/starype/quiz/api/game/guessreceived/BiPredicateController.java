package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiPredicate;

public interface BiPredicateController<T, U> {
    BiPredicateController<T, U> control(BiPredicate<T, U> predicate);
    void setToTrue();
    void setToFalse();
}

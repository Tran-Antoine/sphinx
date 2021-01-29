package net.starype.quiz.api.game.guessprocess;

import java.util.function.Predicate;

public interface PredicateController<T> {
    PredicateController<T> control(Predicate<T> predicate);
    void setToTrue();
    void setToFalse();
}

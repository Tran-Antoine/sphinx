package net.starype.quiz.api.game.guessprocess;

import java.util.function.Predicate;

public interface PredicateBounded<T> {
    void linkTo(Predicate<T> predicate);
}

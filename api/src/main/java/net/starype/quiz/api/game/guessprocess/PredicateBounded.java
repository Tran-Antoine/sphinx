package net.starype.quiz.api.game.guessprocess;

import java.util.function.Predicate;

public interface PredicateBounded<T> {
    PredicateBounded<T> linkTo(Predicate<T> predicate);
}

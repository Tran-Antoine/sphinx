package net.starype.quiz.api.game.guessprocess;

import java.util.function.BiPredicate;

public interface BiPredicateBounded<T, U> {
    BiPredicateBounded<T, U> linkTo(BiPredicate<T, U> predicate);
}

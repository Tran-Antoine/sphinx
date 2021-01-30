package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiPredicate;

public interface BiPredicateBounded<T, U> {
    BiPredicateBounded<T, U> linkTo(BiPredicate<T, U> predicate);
}

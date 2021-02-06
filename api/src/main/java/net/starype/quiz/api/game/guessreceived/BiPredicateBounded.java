package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiPredicate;

/**
 * Represents a functional interface linked to a BiPredicate
 * @param <T> the type of the first argument of the BiPredicate
 * @param <U> the type of the second argument of the BiPredicate
 */
public interface BiPredicateBounded<T, U> {

    /**
     * Link this functional interface to a BiPredicate
     * @param predicate the predicate linked to this functional interface
     * @return this
     */
    BiPredicateBounded<T, U> linkTo(BiPredicate<T, U> predicate);
}

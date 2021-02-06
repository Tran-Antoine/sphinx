package net.starype.quiz.api.game.guessreceived;

import java.util.function.BiPredicate;

/**
 * Represents a functional interface that controls the value of a BiPredicate.
 * This should usually be used with a {@link BiPredicateBounded}
 * @param <T> the type of the first argument of the BiPredicate
 * @param <U> the type of the second argument of the BiPredicate
 */
public interface BiPredicateController<T, U> {
    /**
     * Make this functional interface control a BiPredicate
     * @param predicate the predicate controlled by this functional interface
     * @return this
     */
    BiPredicateController<T, U> control(BiPredicate<T, U> predicate);

    /**
     * Set the controlled BiPredicate to be always true
     */
    void setToTrue();

    /**
     * Set the controlled BiPredicate to be always false
     */
    void setToFalse();
}

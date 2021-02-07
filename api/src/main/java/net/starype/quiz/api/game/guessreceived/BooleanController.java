package net.starype.quiz.api.game.guessreceived;

/**
 * Represents a functional interface that controls the value of a boolean.
 * This should usually be used with a {@link BiPredicateBounded}
 */
public interface BooleanController {

    /**
     * @return : the boolean controlled by this BooleanController
     */
    boolean value();

    /**
     * Set the controlled BiPredicate to be always true
     */
    void setToTrue();

    /**
     * Set the controlled BiPredicate to be always false
     */
    void setToFalse();
}

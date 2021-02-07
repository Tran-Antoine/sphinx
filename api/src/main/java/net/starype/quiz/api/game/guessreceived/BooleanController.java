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
     * Set the controlled boolean to the new value
     * @param newValue the new value of the controlled boolean
     */
    void setControlledBoolean(boolean newValue);
}


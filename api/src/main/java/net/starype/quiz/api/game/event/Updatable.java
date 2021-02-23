package net.starype.quiz.api.game.event;

/**
 * A periodically updatable object.
 * The updatable handler simply takes care of regularly (at a chosen pace) calling the {@link #update(long)} method on their
 * encapsulated updatables.
 * @see UpdatableHandler
 */
public interface Updatable {

    void start(UpdatableHandler updatableHandler);

    void shutDown();

    /**
     * Perform a periodic action
     * @param deltaMillis how many milliseconds went by since the last update call
     */
    void update(long deltaMillis);

    /**
     * Pause the updatable
     * Empty by default.
     */
    default void pause(){}
}

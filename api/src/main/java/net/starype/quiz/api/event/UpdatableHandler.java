package net.starype.quiz.api.event;

/**
 * Handler of a group of Updatables.
 * @see GameUpdatableHandler
 * @see Updatable
 */
public interface UpdatableHandler {

    /**
     * Register an updatable by adding it to the group
     * @param updatable the Updatable to be added in the group
     */
    void registerEvent(Updatable updatable);

    /**
     * Unregister an updatable by removing it from the group
     * @param updatable the Updatable to be removed from the group
     */
    void unregisterEvent(Updatable updatable);

    /**
     * Run all events.
     * This method should usually call the `update` method from the Updatables
     * @see Updatable
     */
    void runAllEvents();

    /**
     * Resets the last millis value.
     * Should be called at the beginning of each new round, since delta times are not calculated between rounds
     * and would thus cause a huge time gap at the beginning of each round.
     */
    void reset();
}

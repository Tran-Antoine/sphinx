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
}

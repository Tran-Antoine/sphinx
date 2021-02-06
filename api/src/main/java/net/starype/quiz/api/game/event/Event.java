package net.starype.quiz.api.game.event;

import net.starype.quiz.api.game.Startable;

public interface Event extends Startable {
    void update(long deltaMillis);
    default void pause(){}
    void shutDown();
}

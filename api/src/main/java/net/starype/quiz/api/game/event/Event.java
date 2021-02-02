package net.starype.quiz.api.game.event;

import net.starype.quiz.api.game.Startable;

public interface Event extends Updatable, Startable {
    default void pause(){}
}

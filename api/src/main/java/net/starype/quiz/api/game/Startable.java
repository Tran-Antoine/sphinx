package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.UpdatableHandler;

public interface Startable {
    void start(UpdatableHandler updatableHandler);
}

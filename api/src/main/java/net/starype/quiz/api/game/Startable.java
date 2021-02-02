package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;

public interface Startable {
    void start(EventHandler eventHandler);
}

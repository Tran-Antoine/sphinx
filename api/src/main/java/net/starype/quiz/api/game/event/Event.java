package net.starype.quiz.api.game.event;

public interface Event {
    void update(long deltaMillis);
    default void pause(){}
}

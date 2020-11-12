package net.starype.quiz.api.game.event;

public interface EventHandler {

    void registerEvent(Event event);
    void unregisterEvent(Event event);
    void runAllEvents();
}

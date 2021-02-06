package net.starype.quiz.api.game.event;

import net.starype.quiz.api.game.EventListener;

import java.util.List;

abstract public class GameEvent implements Event {
    private List<EventListener> eventListeners;

    public void addEventListener(EventListener eventListener) {
        this.eventListeners.add(eventListener);
    }

    public void notifyListeners() {
        eventListeners.forEach(EventListener::onNotified);
    }
}

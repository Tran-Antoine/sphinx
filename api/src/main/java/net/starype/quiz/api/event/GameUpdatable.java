package net.starype.quiz.api.event;

import net.starype.quiz.api.game.EventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class GameUpdatable implements Updatable {
    private List<EventListener> eventListeners = new ArrayList<>();

    public void addEventListener(EventListener eventListener) {
        this.eventListeners.add(eventListener);
    }

    public void notifyListeners() {
        eventListeners.forEach(EventListener::onNotified);
    }
}

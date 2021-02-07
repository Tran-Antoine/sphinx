package net.starype.quiz.api.game.event;

import net.starype.quiz.api.game.EventListener;

import java.util.Collection;
import java.util.LinkedList;

public class GameUpdatableHandler implements UpdatableHandler {

    private long lastMillis;
    private Collection<EventListener> eventListeners;
    private Collection<Updatable> eventsList;

    public GameUpdatableHandler() {
        this(new LinkedList<>());
    }

    public GameUpdatableHandler(Collection<Updatable> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public void registerEvent(Updatable updatable) {
        if(!eventsList.contains(updatable)) {
            eventsList.add(updatable);
        }
    }

    @Override
    public void unregisterEvent(Updatable updatable) {
        eventsList.remove(updatable);
    }

    @Override
    public void runAllEvents() {
        long currentTime = System.currentTimeMillis();
        long deltaMillis = lastMillis == 0
                ? 0
                : currentTime - lastMillis;
        this.lastMillis = currentTime;
        eventsList.forEach((updatable -> updatable.update(deltaMillis)));
    }
}

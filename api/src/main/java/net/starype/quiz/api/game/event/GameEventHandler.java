package net.starype.quiz.api.game.event;

import java.util.Collection;
import java.util.LinkedList;

public class GameEventHandler implements EventHandler {

    private Collection<Event> eventsList;

    public GameEventHandler() {
        this(new LinkedList<>());
    }

    public GameEventHandler(Collection<Event> eventsList) {
        this.eventsList = eventsList;
    }

    @Override
    public void registerEvent(Event event) {
        if(!eventsList.contains(event)) {
            eventsList.add(event);
        }
    }

    @Override
    public void unregisterEvent(Event event) {
        eventsList.remove(event);
    }

    @Override
    public void runAllEvents() {
        eventsList.forEach(Event::run);
    }
}

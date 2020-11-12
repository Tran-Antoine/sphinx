package net.starype.quiz.api.game;

import java.util.LinkedList;
import java.util.List;

public class EventHandler {
    private List<Event> eventsList;

    public EventHandler() {
        this.eventsList = new LinkedList<Event>();
    }

    public void registerEvent(Event event) {
        if(!eventsList.contains(event))
            eventsList.add(event);
    }

    public void unregisterEvent(Event event) {
        eventsList.remove(event);
    }

    public void runAllEvents() {
        for(Event event : eventsList) {
            event.run();
        }
    }

}

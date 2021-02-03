package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.Event;
import net.starype.quiz.api.game.event.EventHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Timer implements Event {
    private long time;
    private TimeUnit unit;
    private Instant startingInstant;
    private Instant currentInstant;
    private EventHandler eventHandler;
    private boolean isEnded;
    private Runnable callBack;


    @Override
    public void start() {
        this.eventHandler = eventHandler;
        eventHandler.registerEvent(this);
        this.startingInstant = Instant.now();
        this.currentInstant = Instant.now();
    }

    @Override
    public void update(long deltaMillis) {
        currentInstant = currentInstant.plusMillis(deltaMillis);

        if(Duration.between(startingInstant, currentInstant).toMillis() > unit.toMillis(time)) {
            this.isEnded = true;
            callBack.run();
            shutDown();
        }
    }

    public void shutDown() {
        eventHandler.unregisterEvent(this);
    }
}

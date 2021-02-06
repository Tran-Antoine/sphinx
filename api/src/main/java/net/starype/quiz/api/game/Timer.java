package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;
import net.starype.quiz.api.game.event.GameEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class Timer extends GameEvent {
    private long time;
    private TimeUnit unit;
    private Instant startingInstant;
    private Instant currentInstant;
    private EventHandler eventHandler;

    public Timer(TimeUnit unit, long time) {
        this.unit = unit;
        this.time = time;
    }

    @Override
    public void start(EventHandler eventHandler) {
        eventHandler.registerEvent(this);
        this.eventHandler = eventHandler;
        this.startingInstant = Instant.now();
        this.currentInstant = Instant.now();
    }

    @Override
    public void update(long deltaMillis) {
        currentInstant = currentInstant.plusMillis(deltaMillis);

        if(Duration.between(startingInstant, currentInstant).toMillis() > unit.toMillis(time)) {
            notifyListeners();
            shutDown();
        }
    }

    public void shutDown() {
        eventHandler.unregisterEvent(this);
    }
}

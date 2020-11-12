package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.Event;
import net.starype.quiz.api.game.event.EventHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeOutEnding implements RoundEndingPredicate, Event {

    private long time;
    private TimeUnit unit;
    private Instant startingInstant;

    private boolean isEnded;
    private Runnable callBack;
    private ScheduledExecutorService task;
    private EventHandler eventHandler;

    public TimeOutEnding(long time, TimeUnit unit) {
        this.unit = unit;
        this.time = time;
    }

    public void startTimer(Runnable checkEndingCallback, EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        eventHandler.registerEvent(this);
        this.startingInstant = Instant.now();
        this.callBack = checkEndingCallback;
    }

    @Override
    public boolean ends() {
        return isEnded;
    }

    @Override
    public void run() {
        if(Duration.between(startingInstant, Instant.now()).toMillis() >
                unit.toMillis(time)) {
            this.isEnded = true;
            callBack.run();
            shutDown();
        }
    }

    public void shutDown() {
        eventHandler.unregisterEvent(this);
    }
}

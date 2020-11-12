package net.starype.quiz.api.game;

import java.time.*;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TimeOutEnding implements RoundEndingPredicate, Event {

    private long time;
    private TimeUnit unit;
    private Instant startingInstant;

    private boolean isEnded;
    private Runnable callBack;
    private ScheduledExecutorService task;
    private EventHandler eventHandler = null;

    public TimeOutEnding(long time, TimeUnit unit) {
        this.unit = unit;
        this.time = time;
    }

    public void startTimer(Runnable checkEndingCallback, EventHandler eventHandler) {
        // Make sure to not start another timers if the object is already registered
        // in another eventHandler
        if(this.eventHandler == null) {
            this.eventHandler = eventHandler;
            eventHandler.registerEvent(this);
            this.startingInstant = Instant.now();
            this.callBack = checkEndingCallback;
        }
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
        if(eventHandler != null) {
            eventHandler.unregisterEvent(this);
            eventHandler = null;
        }
    }
}

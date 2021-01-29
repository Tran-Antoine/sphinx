package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.Updatable;
import net.starype.quiz.api.game.event.UpdatableHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeOutEnding implements RoundEndingPredicate, Updatable {

    private long time;
    private TimeUnit unit;
    private Instant startingInstant;
    private Instant currentInstant;

    private boolean isEnded;
    private Runnable callBack;
    private UpdatableHandler updatableHandler;

    public TimeOutEnding(long time, TimeUnit unit) {
        this.unit = unit;
        this.time = time;
    }

    public void startTimer(Runnable checkEndingCallback, UpdatableHandler updatableHandler) {
        this.updatableHandler = updatableHandler;
        updatableHandler.registerEvent(this);
        this.startingInstant = Instant.now();
        this.currentInstant = Instant.now();
        this.callBack = checkEndingCallback;
    }

    @Override
    public boolean ends() {
        return isEnded;
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
        updatableHandler.unregisterEvent(this);
    }
}

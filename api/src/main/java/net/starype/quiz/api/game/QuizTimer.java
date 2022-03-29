package net.starype.quiz.api.game;

import net.starype.quiz.api.event.GameUpdatable;
import net.starype.quiz.api.event.UpdatableHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class QuizTimer extends GameUpdatable implements EventListener {
    private long time;
    private final long shortenedTime;
    private final TimeUnit unit;
    private Instant startingInstant;
    private Instant currentInstant;
    private UpdatableHandler updatableHandler;

    public QuizTimer(TimeUnit unit, long time) {
        this(unit, time, time);
    }

    public QuizTimer(TimeUnit unit, long time, long shortenedTime) {
        this.unit = unit;
        this.time = time;
        this.shortenedTime = shortenedTime;
    }

    @Override
    public void start(UpdatableHandler updatableHandler) {
        updatableHandler.registerEvent(this);
        this.updatableHandler = updatableHandler;
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

    @Override
    public void shutDown() {
        updatableHandler.unregisterEvent(this);
    }

    public long millisLeft() {
        return unit.toMillis(time) - Duration.between(startingInstant, currentInstant).toMillis();
    }

    private void shortenRemaining() {
        if(millisLeft() < unit.toMillis(shortenedTime)) {
            return;
        }
        this.time = shortenedTime;
        this.startingInstant = Instant.now();
    }

    @Override
    public void onNotified() {
        shortenRemaining();
    }
}

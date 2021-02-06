package net.starype.quiz.api.game;

import net.starype.quiz.api.game.event.EventHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeOutEnding implements RoundEndingPredicate {

    private long time;
    private TimeUnit unit;
    private Instant startingInstant;
    private Instant currentInstant;

    private boolean isEnded;
    private Runnable callBack;
    private EventHandler eventHandler;
    private List<Observer> observers = new ArrayList<>();

    private Timer timer;

    public TimeOutEnding(long time, TimeUnit unit) {
        this.unit = unit;
        this.time = time;
    }

    public TimeOutEnding(Timer timer) {
        this.timer = timer;
    }

    @Override
    public boolean ends() {
        return false;
    }

}

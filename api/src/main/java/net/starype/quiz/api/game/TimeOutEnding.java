package net.starype.quiz.api.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeOutEnding implements RoundEndingPredicate, Runnable {

    private long time;
    private TimeUnit unit;

    private boolean isEnded;

    public TimeOutEnding(long time, TimeUnit unit) {
        this.unit = unit;
        this.time = time;
    }

    public void startTimer() {
        ScheduledExecutorService task = Executors.newScheduledThreadPool(1);
        task.schedule(this, time, unit);
    }

    @Override
    public boolean ends() {
        return isEnded;
    }

    @Override
    public void run() {
        this.isEnded = true;
    }
}

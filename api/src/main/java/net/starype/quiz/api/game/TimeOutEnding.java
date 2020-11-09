package net.starype.quiz.api.game;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class TimeOutEnding implements RoundEndingPredicate, Runnable {

    private long time;
    private TimeUnit unit;

    private boolean isEnded;
    private Runnable callBack;
    private ScheduledExecutorService task;

    public TimeOutEnding(long time, TimeUnit unit) {
        this.unit = unit;
        this.time = time;
    }

    public void startTimer(Runnable checkEndingCallback) {
        this.callBack = checkEndingCallback;
        this.task = Executors.newScheduledThreadPool(1);
        task.schedule(this, time, unit);
    }

    @Override
    public boolean ends() {
        return isEnded;
    }

    @Override
    public void run() {
        this.isEnded = true;
        callBack.run();
        shutDown();
    }

    public void shutDown() {
        task.shutdown();
    }
}

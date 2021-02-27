package net.starype.quiz.discordimpl.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CounterLimiter {
    private final AtomicInteger counter;
    private final List<Long> instances;
    private final double maxAwaitingTime;
    private final int maxCount;

    public CounterLimiter(int maxCount, double maxAwaitingTime) {
        this.maxCount = maxCount;
        this.maxAwaitingTime = maxAwaitingTime;
        counter = new AtomicInteger(0);
        instances = new LinkedList<>();
    }

    public CounterLimiter(int maxCount) {
        this(maxCount, 5.0);
    }

    public synchronized boolean acquireInstance() {
        double awaitingTime = .0;
        if(instances.contains(Thread.currentThread().getId())) {
            return true;
        }
        while(true) {
            if(counter.getAndUpdate(i -> i < maxCount ? (i + 1) : (i)) < maxCount) {
                instances.add(Thread.currentThread().getId());
                return true;
            }
            try {
                Thread.sleep(500);
                awaitingTime += .5;
            }
            catch (InterruptedException ignored) { return false; }
            if(awaitingTime > maxAwaitingTime) {
                return false;
            }
        }
    }

    public synchronized void releaseInstance() {
        if(!instances.contains(Thread.currentThread().getId())) {
            throw new RuntimeException("Cannot release an non acquire instance");
        }
        instances.removeIf(i -> i.equals(Thread.currentThread().getId()));
        if(counter.decrementAndGet() < 0) {
            throw new IllegalThreadStateException();
        }
    }

    public int maxCount() {
        return maxCount;
    }

    public synchronized void releaseInstanceIfNotPresent() {
        if(instances.contains(Thread.currentThread().getId())) {
            releaseInstance();
        }
    }
}

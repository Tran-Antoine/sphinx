package net.starype.quiz.discordimpl.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class CounterLimiter {
    private final AtomicInteger counter;
    private final List<Long> instances;
    private final int maxCount;

    public CounterLimiter(int maxCount) {
        this.maxCount = maxCount;
        counter = new AtomicInteger(0);
        instances = new LinkedList<>();
    }

    public synchronized boolean acquireInstance(long uniqueId) {
        if(instances.contains(uniqueId)) {
            return true;
        }
        if(counter.getAndUpdate(i -> i < maxCount ? (i + 1) : (i)) < maxCount) {
            instances.add(uniqueId);
            return true;
        }
        return false;
    }

    public synchronized void releaseInstance(long uniqueId) {
        if(!instances.contains(uniqueId)) {
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

    public synchronized void releaseInstanceIfNotPresent(long uniqueId) {
        if(instances.contains(uniqueId)) {
            releaseInstance(uniqueId);
        }
    }
}

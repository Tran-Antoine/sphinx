package net.starype.quiz.discordimpl.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CounterLimiter {
    private final List<Long> instances;
    private final int maxCount;

    public CounterLimiter(int maxCount) {
        this.maxCount = maxCount;
        instances = Collections.synchronizedList(new LinkedList<>());
    }

    public synchronized boolean register(long uniqueId) {
        if(instances.contains(uniqueId)) {
            return true;
        }
        if(instances.size() + 1 <= maxCount) {
            instances.add(uniqueId);
            return true;
        }
        return false;
    }

    public synchronized void unregister(long uniqueId) {
        if(!instances.contains(uniqueId)) {
            //throw new RuntimeException("Cannot unregister a non-registered id");
            return;
        }
        instances.removeIf(i -> i.equals(Thread.currentThread().getId()));
    }

    public int maxCount() {
        return maxCount;
    }

    public synchronized void unregisterIfPresent(long uniqueId) {
        if(instances.contains(uniqueId)) {
            unregister(uniqueId);
        }
    }
}

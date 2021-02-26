package net.starype.quiz.discordimpl.util;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class DiscordContext {

    private final CounterLimiter downloadingLimiter;
    private final CounterLimiter lobbyLimiter;

    public static class CounterLimiter {
        private final AtomicInteger counter;
        private final List<Integer> instances;
        private static final double maxAwaitingTime = 5.0;
        private final int maxCount;

        public CounterLimiter(int maxCount) {
            this.maxCount = maxCount;
            counter = new AtomicInteger(0);
            instances = new LinkedList<>();
        }

        public synchronized boolean acquireInstance(Object instance) {
            double awaitingTime = .0;
            if(instances.contains(instance.hashCode())) {
                return true;
            }
            while(true) {
                if(counter.getAndUpdate(i -> i < maxCount ? (i + 1) : (i)) < maxCount) {
                    instances.add(instance.hashCode());
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

        public synchronized void releaseInstance(Object instance) {
            if(!instances.contains(instance.hashCode())) {
                throw new RuntimeException("Cannot release an non acquire instance");
            }
            instances.removeIf(i -> i.equals(instance.hashCode()));
            if(counter.decrementAndGet() < 0) {
                throw new IllegalThreadStateException();
            }
        }
    }

    public DiscordContext() {
        downloadingLimiter = new CounterLimiter(5);
        lobbyLimiter = new CounterLimiter(10);
    }

    public final CounterLimiter downloadingLimiter() {
        return downloadingLimiter;
    }

    public CounterLimiter lobbyLimiter() {
        return lobbyLimiter;
    }
}

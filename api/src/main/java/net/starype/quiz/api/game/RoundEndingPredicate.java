package net.starype.quiz.api.game;

import java.util.function.Supplier;

public interface RoundEndingPredicate extends Supplier<Boolean> {

    default RoundEndingPredicate or(RoundEndingPredicate other) {
        return () -> this.get() || other.get();
    }

    default RoundEndingPredicate and(RoundEndingPredicate other) {
        return () -> this.get() && other.get();
    }
}

package net.starype.quiz.api.game;

import java.util.function.Supplier;

public interface RoundEndingPredicate {

    boolean ends();

    default RoundEndingPredicate or(RoundEndingPredicate other) {
        return () -> this.ends() || other.ends();
    }

    default RoundEndingPredicate and(RoundEndingPredicate other) {
        return () -> this.ends() && other.ends();
    }
}

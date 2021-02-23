package net.starype.quiz.api.game;

public interface EndingPredicate {

    boolean ends();

    default EndingPredicate or(EndingPredicate other) {
        return () -> this.ends() || other.ends();
    }

    default EndingPredicate and(EndingPredicate other) {
        return () -> this.ends() && other.ends();
    }
}

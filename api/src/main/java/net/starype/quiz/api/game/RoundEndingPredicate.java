package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;

public interface RoundEndingPredicate {

    boolean ends(RoundState roundState);

    default RoundEndingPredicate or(RoundEndingPredicate other) {
        return roundState -> this.ends(roundState) || other.ends(roundState);
    }

    default RoundEndingPredicate and(RoundEndingPredicate other) {
        return roundState -> this.ends(roundState) && other.ends(roundState);
    }
}

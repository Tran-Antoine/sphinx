package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;

public interface RoundEndingPredicate {

    boolean ends();

    void initRoundState(RoundState roundState);

    default RoundEndingPredicate or(RoundEndingPredicate other) {
        return new RoundEndingPredicate() {
            @Override
            public boolean ends() {
                return RoundEndingPredicate.this.ends() || other.ends();
            }

            @Override
            public void initRoundState(RoundState roundState) {
                RoundEndingPredicate.this.initRoundState(roundState);
                other.initRoundState(roundState);
            }
        };
    }

    default RoundEndingPredicate and(RoundEndingPredicate other) {
        return new RoundEndingPredicate() {
            @Override
            public boolean ends() {
                return RoundEndingPredicate.this.ends() && other.ends();
            }

            @Override
            public void initRoundState(RoundState roundState) {
                RoundEndingPredicate.this.initRoundState(roundState);
                other.initRoundState(roundState);
            }
        };
    }
}

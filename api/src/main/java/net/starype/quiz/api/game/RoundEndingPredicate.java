package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.RoundState;

public interface RoundEndingPredicate {

    boolean ends();

    void initRoundState(RoundState roundState);

    default RoundEndingPredicate or(RoundEndingPredicate other) {
        RoundEndingPredicate first = this;
        return new RoundEndingPredicate() {
            @Override
            public boolean ends() {
                return first.ends() || other.ends();
            }

            @Override
            public void initRoundState(RoundState roundState) {
                first.initRoundState(roundState);
                other.initRoundState(roundState);
            }
        };
    }

    default RoundEndingPredicate and(RoundEndingPredicate other) {
        RoundEndingPredicate first = this;
        return new RoundEndingPredicate() {
            @Override
            public boolean ends() {
                return first.ends() && other.ends();
            }

            @Override
            public void initRoundState(RoundState roundState) {
                first.initRoundState(roundState);
                other.initRoundState(roundState);
            }
        };
    }
}

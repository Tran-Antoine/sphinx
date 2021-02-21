package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;

public abstract class RoundEndingPredicate implements EndingPredicate {
    private RoundState roundState;

    public RoundEndingPredicate(RoundState roundState) {
        this.roundState = roundState;
    }

    public RoundState getRoundState() {
        return roundState;
    }
}

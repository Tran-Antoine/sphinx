package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;

public class NoPlayerEligible extends RoundEndingPredicate {

    public NoPlayerEligible(RoundState roundState) {
        super(roundState);
    }

    @Override
    public boolean ends() {
        return !getRoundState().existsEligible();
    }
}

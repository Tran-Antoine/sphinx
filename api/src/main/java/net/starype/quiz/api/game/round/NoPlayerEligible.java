package net.starype.quiz.api.game.round;

import net.starype.quiz.api.game.RoundEndingPredicate;

public class NoPlayerEligible extends RoundEndingPredicate {

    public NoPlayerEligible(RoundState roundState) {
        super(roundState);
    }

    @Override
    public boolean ends() {
        return !getRoundState().getPlayerEligibility()
                .existsEligible(getRoundState().getPlayers());
    }
}

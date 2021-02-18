package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;

public class SwitchPredicate implements RoundEndingPredicate, EventListener {
    private boolean state;

    public SwitchPredicate(boolean initial) {
        this.state = initial;
    }

    @Override
    public boolean ends(RoundState roundState) {
        return state;
    }

    @Override
    public void onNotified() {
        state = !state;
    }
}

package net.starype.quiz.api.game;

import net.starype.quiz.api.round.RoundState;

public class SwitchPredicate extends RoundEndingPredicate implements EventListener {
    private boolean state;

    public SwitchPredicate(boolean initial, RoundState roundState) {
        super(roundState);
        this.state = initial;
    }

    @Override
    public boolean ends() {
        return state;
    }

    @Override
    public void onNotified() {
        state = !state;
    }
}

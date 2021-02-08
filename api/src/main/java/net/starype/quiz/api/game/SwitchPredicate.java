package net.starype.quiz.api.game;

import net.starype.quiz.api.game.guessreceived.RoundState;

public class SwitchPredicate implements RoundEndingPredicate, EventListener {
    private boolean state;

    public SwitchPredicate(boolean initial) {
        this.state = initial;
    }

    @Override
    public boolean ends() {
        return state;
    }

    @Override
    public void initRoundState(RoundState roundState) {}

    @Override
    public void onNotified() {
        state = !state;
    }
}

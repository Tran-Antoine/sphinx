package net.starype.quiz.api.game;

public class SwitchPredicate implements RoundEndingPredicate, EventListener {
    private boolean state = false;

    public SwitchPredicate(boolean initial) {
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

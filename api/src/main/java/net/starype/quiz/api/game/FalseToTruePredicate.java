package net.starype.quiz.api.game;

public class FalseToTruePredicate implements RoundEndingPredicate, EventListener {
    boolean state = false;

    @Override
    public boolean ends() {
        return state;
    }

    @Override
    public void onNotified() {
        state = true;
    }
}

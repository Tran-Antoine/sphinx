package net.starype.quiz.api.game;

public class CheckEndOfRound implements EventListener {

    Runnable callback;

    public CheckEndOfRound(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void onNotified() {
        callback.run();
    }
}

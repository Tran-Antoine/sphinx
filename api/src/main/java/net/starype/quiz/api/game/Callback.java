package net.starype.quiz.api.game;

public class Callback implements EventListener {

    Runnable callback;

    public Callback(Runnable callback) {
        this.callback = callback;
    }

    @Override
    public void onNotified() {
        callback.run();
    }
}

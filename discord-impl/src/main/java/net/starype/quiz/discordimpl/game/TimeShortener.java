package net.starype.quiz.discordimpl.game;

import net.starype.quiz.api.event.GameUpdatable;
import net.starype.quiz.api.event.UpdatableHandler;
import net.starype.quiz.api.game.GuessCounter;
import net.starype.quiz.api.game.QuizTimer;

public class TimeShortener extends GameUpdatable {

    private final GuessCounter counter;
    private UpdatableHandler updatableHandler;

    public TimeShortener(GuessCounter counter) {
        this.counter = counter;
    }

    @Override
    public void start(UpdatableHandler updatableHandler) {
        this.updatableHandler = updatableHandler;
        updatableHandler.registerEvent(this);
    }

    @Override
    public void shutDown() {
        updatableHandler.unregisterEvent(this);
    }

    @Override
    public void update(long deltaMillis) {
        if(!counter.isEmpty()) {
            notifyListeners();
            shutDown();
        }
    }
}

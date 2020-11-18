package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

import java.util.concurrent.atomic.AtomicReference;

public class WinnerExists implements RoundEndingPredicate {

    private AtomicReference<? extends IDHolder> container;

    public WinnerExists(AtomicReference<? extends IDHolder> container) {
        this.container = container;
    }

    @Override
    public boolean ends() {
        return container.get() != null;
    }
}

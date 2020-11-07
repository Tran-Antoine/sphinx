package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.concurrent.atomic.AtomicReference;

public class WinnerExists implements RoundEndingPredicate {

    private AtomicReference<? extends UUIDHolder> container;

    public WinnerExists(AtomicReference<? extends UUIDHolder> container) {
        this.container = container;
    }

    @Override
    public boolean ends() {
        return container.get() != null;
    }
}

package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.util.ObjectContainer;

public class WinnerExists implements RoundEndingPredicate {

    private ObjectContainer<UUIDHolder> container;

    public WinnerExists(ObjectContainer<UUIDHolder> container) {
        this.container = container;
    }

    @Override
    public boolean ends() {
        return container.exists();
    }
}

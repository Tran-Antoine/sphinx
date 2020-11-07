package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Collection;

public class NoGuessLeft implements RoundEndingPredicate {

    private MaxGuessCounter counter;
    private Collection<? extends UUIDHolder> players;

    public NoGuessLeft(MaxGuessCounter counter, Collection<? extends UUIDHolder> players) {
        this.counter = counter;
        this.players = players;
    }

    @Override
    public boolean ends() {
        return !counter.existsEligible(players);
    }
}

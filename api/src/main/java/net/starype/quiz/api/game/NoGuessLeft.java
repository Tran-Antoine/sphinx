package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

public class NoGuessLeft implements RoundEndingPredicate {

    private MaxGuessCounter counter;
    private Collection<? extends IDHolder> players;

    public NoGuessLeft(MaxGuessCounter counter, Collection<? extends IDHolder> players) {
        this.counter = counter;
        this.players = players;
    }

    @Override
    public boolean ends() {
        return !counter.existsEligible(players);
    }
}

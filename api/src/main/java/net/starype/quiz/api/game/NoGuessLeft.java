package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Collection;

public class NoGuessLeft implements RoundEndingPredicate, PlayerSettable {

    private MaxGuessCounter counter;
    private Collection<? extends IDHolder<?>> players;

    public NoGuessLeft(MaxGuessCounter counter) {
        this.counter = counter;
    }

    @Override
    public boolean ends() {
        return !counter.existsEligible(players);
    }

    @Override
    public void setPlayers(Collection<? extends Player<?>> players) {
        this.players = players;
    }
}

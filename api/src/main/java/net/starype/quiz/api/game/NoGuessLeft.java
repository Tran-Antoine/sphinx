package net.starype.quiz.api.game;

import net.starype.quiz.api.game.round.RoundState;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.Collection;

public class NoGuessLeft extends RoundEndingPredicate {

    private MaxGuessCounter counter;
    private Collection<? extends IDHolder<?>> players;

    public NoGuessLeft(RoundState roundState) {
        super(roundState);
    }

    @Override
    public boolean ends() {
        this.counter = getRoundState().getCounter();
        this.players = getRoundState().getPlayers();
        return !counter.existsEligible(players);
    }
}

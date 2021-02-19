package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.round.RoundState;

import java.util.Collection;

public class NoGuessLeft extends RoundEndingPredicate {

    public NoGuessLeft(RoundState roundState) {
        super(roundState);
    }

    @Override
    public boolean ends() {
        MaxGuessCounter counter = getRoundState().getCounter();
        Collection<? extends Player<?>> players = getRoundState().getPlayers();
        return !counter.existsEligible(players);
    }
}

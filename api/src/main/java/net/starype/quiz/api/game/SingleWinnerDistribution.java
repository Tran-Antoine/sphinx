package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.concurrent.atomic.AtomicReference;

public class SingleWinnerDistribution implements ScoreDistribution {

    private AtomicReference<? extends IDHolder<?>> container;
    private double scoreForWinner;

    public SingleWinnerDistribution(AtomicReference<? extends IDHolder<?>> container) {
        this(container, 1.0);
    }

    public SingleWinnerDistribution(AtomicReference<? extends IDHolder<?>> container, double scoreForWinner) {
        this.container = container;
        this.scoreForWinner = scoreForWinner;
    }

    @Override
    public Double apply(Player player) {
        if(container.get() == null) {
            throw new IllegalStateException("No winner was established");
        }
        return player.getId().equals(container.get().getId())
                ? scoreForWinner
                : 0.0;
    }
}

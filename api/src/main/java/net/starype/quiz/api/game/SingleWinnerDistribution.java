package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.concurrent.atomic.AtomicReference;

public class SingleWinnerDistribution implements ScoreDistribution {

    private AtomicReference<? extends UUIDHolder> container;
    private double scoreForWinner;

    public SingleWinnerDistribution(AtomicReference<? extends UUIDHolder> container) {
        this(container, 1.0);
    }

    public SingleWinnerDistribution(AtomicReference<? extends UUIDHolder> container, double scoreForWinner) {
        this.container = container;
        this.scoreForWinner = scoreForWinner;
    }

    @Override
    public Double apply(Player player) {
        if(container.get() == null) {
            return 0.0;
        }
        return player.getUUID().equals(container.get().getUUID())
                ? scoreForWinner
                : 0.0;
    }
}

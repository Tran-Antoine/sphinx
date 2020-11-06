package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;
import net.starype.quiz.api.util.ObjectContainer;

public class SingleWinnerDistribution implements ScoreDistribution {

    private ObjectContainer<? extends UUIDHolder> container;
    private double scoreForWinner;

    public SingleWinnerDistribution(ObjectContainer<? extends UUIDHolder> container) {
        this(container, 1);
    }

    public SingleWinnerDistribution(ObjectContainer<? extends UUIDHolder> container, double scoreForWinner) {
        this.container = container;
        this.scoreForWinner = scoreForWinner;
    }

    @Override
    public Double apply(Player player) {
        return player.getUUID().equals(container.get().getUUID())
                ? scoreForWinner
                : 0.0;
    }
}

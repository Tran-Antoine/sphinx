package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.IDHolder;
import net.starype.quiz.api.game.player.Player;

import java.util.Map;

public class BinaryDistribution implements ScoreDistribution {
    double threshold;
    Map<? extends IDHolder<?>, Double> playersCorrectness;
    private double scoreForWinner;

    public BinaryDistribution(double threshold, Map<? extends IDHolder<?>, Double> playersCorrectness,
                              double scoreForWinner) {
        this.threshold = threshold;
        this.playersCorrectness = playersCorrectness;
        this.scoreForWinner = scoreForWinner;
    }

    public BinaryDistribution(Map<? extends IDHolder<?>, Double> playersCorrectness, double scoreForWinner) {
        this(0.5, playersCorrectness, scoreForWinner);
    }

    @Override
    public Double apply(Player<?> player) {
        return playersCorrectness.get(player) < threshold
                ? 0.0
                : scoreForWinner;
    }
}

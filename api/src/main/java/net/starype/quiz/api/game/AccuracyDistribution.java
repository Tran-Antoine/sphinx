package net.starype.quiz.api.game;

import net.starype.quiz.api.player.IDHolder;
import net.starype.quiz.api.player.Player;

import java.util.HashMap;
import java.util.Map;

public class AccuracyDistribution implements ScoreDistribution {

    private double maxToAward;
    private Map<IDHolder<?>, Double> accuracies;

    public AccuracyDistribution(double maxToAward) {
        this.maxToAward = maxToAward;
        this.accuracies = new HashMap<>();
    }

    public void updateScore(IDHolder<?> holder, double accuracy) {
        double currentAccuracy = accuracies.getOrDefault(holder, -1D);
        if(currentAccuracy < accuracy) {
            accuracies.put(holder, accuracy);
        }
    }

    @Override
    public Double apply(Player player) {
        return maxToAward * accuracies.getOrDefault(player, 0D);
    }
}

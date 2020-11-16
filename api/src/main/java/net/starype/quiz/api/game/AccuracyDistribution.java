package net.starype.quiz.api.game;

import net.starype.quiz.api.game.ScoreDistribution;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Map;

public class AccuracyDistribution implements ScoreDistribution {

    private double maxToAward;
    private Map<UUIDHolder, Double> accuracies;

    public AccuracyDistribution(double maxToAward) {
        this.maxToAward = maxToAward;
    }

    public void updateScore(UUIDHolder holder, double accuracy) {
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

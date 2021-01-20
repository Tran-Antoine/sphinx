package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.IDHolder;

import java.util.HashMap;
import java.util.Map;

public class OneTryDistribution implements ScoreDistribution {

    private double maxToAward;
    private Map<IDHolder<?>, Double> accuracies;

    public OneTryDistribution(double maxToAward) {
        this.maxToAward = maxToAward;
        this.accuracies = new HashMap<>();
    }

    public void addIfNew(IDHolder<?> player, double accuracy) {
        if(accuracies.containsKey(player)) {
            return;
        }
        accuracies.put(player, accuracy);
    }

    @Override
    public Double apply(Player player) {
        return maxToAward * accuracies.getOrDefault(player, 0D);
    }
}

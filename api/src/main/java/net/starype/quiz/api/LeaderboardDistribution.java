package net.starype.quiz.api;

import net.starype.quiz.api.game.ScoreDistribution;
import net.starype.quiz.api.game.player.Player;
import net.starype.quiz.api.game.player.UUIDHolder;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

public class LeaderboardDistribution implements ScoreDistribution {

    private double maxAwarded;
    private TreeMap<UUIDHolder, Double> scores;

    public LeaderboardDistribution(double maxAwarded) {
        this.maxAwarded = maxAwarded;
        this.scores = new TreeMap<>(getComparator());
    }

    public void put(UUIDHolder player, double score) {
        double current = scores.getOrDefault(player, 0D);
        if(current < score) {
            scores.put(player, score);
        }
    }

    @Override
    public Double apply(Player player) {
        throw new IllegalStateException("Can't resolve apply call for a single player. Use applyAll instead");
    }

    @Override
    public void applyAll(Collection<? extends Player> players, BiConsumer<Player, Double> action) {
        for(UUIDHolder holder : scores.keySet()) {

        }
    }

    private Comparator<UUIDHolder> getComparator() {
        return Comparator.comparing(a -> scores.getOrDefault(a, 0D));
    }

    /*private static class Distribution {

        private double step;
        private double currentToAward;

        private Distribution(double maxToAward, double minimumToAward, int totalPlayerCount) {
            this.step = (maxToAward - minimumToAward) / totalPlayerCount;
            this.currentToAward = maxToAward;
        }

        private double getNext() {
            double value = currentToAward;
            currentToAward -= step;
            return value;
        }

        private Distribution reset() {
            return new Distribution(maxToAward, minimumToAward, totalPlayer)
        }
    }*/
}

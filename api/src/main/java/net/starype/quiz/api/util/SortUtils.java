package net.starype.quiz.api.util;

import net.starype.quiz.api.game.player.Player;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SortUtils {

    public static List<Standing> sortByScore(Map<Player<?>, Double> map) {
        return map
                .entrySet()
                .stream()
                .map(entry -> new Standing(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingDouble(a -> a.scoreAcquired))
                .collect(Collectors.toList());
    }

    public static class Standing {

        private Player<?> player;
        private double scoreAcquired;

        public Standing(Player<?> player, double scoreAcquired) {
            this.player = player;
            this.scoreAcquired = scoreAcquired;
        }

        public double getScoreAcquired() {
            return scoreAcquired;
        }

        public Player<?> getPlayer() {
            return player;
        }
    }
}

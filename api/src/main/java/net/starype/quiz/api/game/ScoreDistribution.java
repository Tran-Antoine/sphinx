package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ScoreDistribution extends Function<Player<?>, Double> {

    default List<Standing> applyAll(Collection<? extends Player<?>> players, BiConsumer<Player<?>, Double> action) {
        List<Standing> standings = players
                .stream()
                .map(player -> new Standing(player, this.apply(player)))
                .sorted()
                .collect(Collectors.toList());
        return standings;
    }

    class Standing implements Comparable<Standing> {

        private final Player<?> player;
        private final double scoreAcquired;

        public Standing(Player<?> player, double score) {
            this.player = player;
            this.scoreAcquired = score;
        }

        @Override
        public int compareTo(Standing o) {
            return Double.compare(o.scoreAcquired, scoreAcquired);
        }

        public Player<?> getPlayer() {
            return player;
        }

        public double getScoreAcquired() {
            return scoreAcquired;
        }
    }
}

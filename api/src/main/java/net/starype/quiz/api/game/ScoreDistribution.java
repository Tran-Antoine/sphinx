package net.starype.quiz.api.game;

import net.starype.quiz.api.game.player.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface ScoreDistribution extends Function<Player<?>, Double> {

    double EPSILON = 0.001;

    default Map<Player<?>, Double> applyAll(Collection<? extends Player<?>> players, BiConsumer<Player<?>, Double> action) {
        Map<Player<?>, Double> standings = new HashMap<>();
        for(Player<?> player : players) {
            double score = this.apply(player);
            action.accept(player, score);
            standings.put(player, score);
        }
        return standings;
    }
}
